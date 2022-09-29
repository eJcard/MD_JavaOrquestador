package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;
import uy.com.md.sistar.dto.in.ReverseRequestDto;
import uy.com.md.sistar.dto.in.TransferUpdateRequestDto;
import uy.com.md.sistar.dto.out.ReverseResponseDto;
import uy.com.md.sistar.dto.out.TransferResponseDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.*;
import uy.com.md.sistar.util.ExternalIdUtils;
import uy.com.md.sistar.util.SistarConstants;

@Service
public class ReversalService {
  private static final Logger logger = LoggerFactory.getLogger(ReversalService.class);
  private final ModelMapper modelMapper;
  private final SistarClient sistarClientWSMidinero;
  private final MensajeService messageService;
  private final CardCreditService cardCreditService;
  private final ServiceUtils serviceUtils;
  private final SistarClient sistarClientC2BMultiproc;
  private final C2CService c2cService;
  private final C2BService c2bService;

  public ReversalService(ModelMapper modelMapper,
                         MensajeService messageService,
                         @Qualifier("SistarClientWSMidinero") SistarClient sistarClientWSMidinero,
                         @Qualifier("SistarClientC2BMultiproc") SistarClient sistarClientC2BMultiproc,
                         CardCreditService cardCreditService,
                         C2CService c2cService,
                         C2BService c2bService,
                         ServiceUtils serviceUtils
  ){
    this.modelMapper = modelMapper;
    this.sistarClientWSMidinero = sistarClientWSMidinero;
    this.messageService = messageService;
    this.cardCreditService = cardCreditService;
    this.serviceUtils = serviceUtils;
    this.sistarClientC2BMultiproc = sistarClientC2BMultiproc;
    this.c2cService = c2cService;
    this.c2bService = c2bService;
  }

  @CircuitBreaker(name = "reverse", fallbackMethod = "reverseFallback")
  public InternalResponseDto<ReverseResponseDto> reverso(ReverseRequestDto requestDto) {
    String channel = StringUtils.defaultIfBlank(requestDto.getCanal(), "recargaGenerica");

    switch (channel) {
      case "recargaGenerica":
      case "terceros":
      case "creditos":
      case "sar":
        return reversoPorDocumento(requestDto);

      case "debitosAutomaticos":
      case "otrosProcesadores":
        if(Boolean.TRUE.equals(requestDto.getReversaRecarga())){
          // Si se solicita reverso de una recarga, solicito reverso a travez de metodo de ajustes por documento en API Sistar
          return reversoPorCuenta(requestDto);
        }else{
          // Si se solicita reverso de un consumo, solicito reverso a travez de metodo de reverso de C2B
          TransferUpdateRequestDto reverseC2P = new TransferUpdateRequestDto();
          reverseC2P.setCuenta(requestDto.getCuenta());
          reverseC2P.setRrn(requestDto.getToReverseRrn());
          reverseC2P.setCanal(requestDto.getCanal());

          InternalResponseDto<TransferResponseDto> revResponse = c2bService.reverse(reverseC2P);

          ReverseResponseDto resp = new ReverseResponseDto();
          resp.setSuccess(revResponse.getRespuesta() != null && revResponse.getRespuesta().getSuccess());
          revResponse.setResultado(resp.getSuccess());

          InternalResponseDto<ReverseResponseDto> responseReverseC2P = new InternalResponseDto<>(resp);
          responseReverseC2P.setResultado(resp.getSuccess());
          return responseReverseC2P;
        }

      case "midinero": // C2C
        return c2cService.reverse(requestDto);

      case "transferenciaBanco": // C2b
        TransferUpdateRequestDto reverseC2B = new TransferUpdateRequestDto();
        reverseC2B.setCuenta(requestDto.getCuenta());
        reverseC2B.setCanal(requestDto.getCanal());
        reverseC2B.setRrn(requestDto.getToReverseRrn());

        InternalResponseDto<TransferResponseDto> reverseC2BResult = c2bService.reverse(reverseC2B);

        if(reverseC2BResult.getRespuesta() != null && reverseC2BResult.getRespuesta().getSuccess()){
          ReverseResponseDto convertedResponse = new ReverseResponseDto();
          convertedResponse.setItc(SistarConstants.ITC_C2B);
          convertedResponse.setSuccess(reverseC2BResult.getRespuesta().getSuccess());
          return new InternalResponseDto<>(convertedResponse);
        }else{
          InternalResponseDto<ReverseResponseDto> errorResp = new InternalResponseDto<>(new ReverseResponseDto());
          errorResp.getRespuesta().setSuccess(false);
          errorResp.setResultado(false);
          reverseC2BResult.getMensajes()
              .forEach(errorResp::addMensaje);
          return errorResp;
        }

      default:
        throw new SistarbancOperationException("channel.unsupported", "Canal no soportado");
    }
  }

  private InternalResponseDto<ReverseResponseDto> reverseFallback(ReverseRequestDto requestDto, Exception ex){
    return serviceUtils.crackResponse(requestDto, ex);
  }

  private InternalResponseDto<ReverseResponseDto> reversoPorDocumento(ReverseRequestDto requestDto) {
    InternalResponseDto<ReverseResponseDto> response;
    WSPagosAjustesxCIResponse responseSOAP = null;
    WSPagosAjustesxCI requestSoap = modelMapper.map(requestDto, WSPagosAjustesxCI.class);

    if (StringUtils.isEmpty(requestSoap.getRequestExternalIdARevertir())) {
      requestSoap.setRequestExternalId(ExternalIdUtils.getIdAsString(SistarConstants.MAX_EXTERNAL_ID_TRANSACTIONS));
    }
    responseSOAP = sistarClientWSMidinero.reversoPorDocumento(requestSoap);
    PagosAjustesInfo result = responseSOAP.getWSPagosAjustesxCIResult();

    if (result.getCodRetorno().isEmpty() || NumberUtils.toInt(result.getCodRetorno(), 1) == 0) {
      ReverseResponseDto responseDto = modelMapper.map(result, ReverseResponseDto.class);
      response = new InternalResponseDto<>(responseDto);
      return response;
    } else {
      throw new SistarbancOperationException(result.getCodRetorno(), result.getMensajeRetorno());
    }
  }

  private InternalResponseDto<ReverseResponseDto> reversoPorCuenta(ReverseRequestDto requestDto) {
    InternalResponseDto<ReverseResponseDto> response;
    WSPagosAjustesResponse responseSOAP = null;
    WSPagosAjustes requestSoap = modelMapper.map(requestDto, WSPagosAjustes.class);

    responseSOAP = sistarClientWSMidinero.reversoPorCuenta(requestSoap);
    PagosAjustesInfo result = responseSOAP.getWSPagosAjustesResult();
    boolean success = result.getCodRetorno().isEmpty() || NumberUtils.toInt(result.getCodRetorno(), 1) == 0;

    if (success) {
      ReverseResponseDto responseDto = modelMapper.map(result, ReverseResponseDto.class);
      response = new InternalResponseDto<>(responseDto);
      return response;
    } else {
      throw new SistarbancOperationException(result.getCodRetorno(), result.getMensajeRetorno());
    }
  }
}
