package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeDetail;
import uy.com.md.sistar.dto.in.ReverseRequestDto;
import uy.com.md.sistar.dto.in.TransferRequestDto;
import uy.com.md.sistar.dto.in.TransferUpdateRequestDto;
import uy.com.md.sistar.dto.out.ReverseResponseDto;
import uy.com.md.sistar.dto.out.TransferResponseDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client2.SistarbancMDTrfC2BAUTORIZACION;
import uy.com.md.sistar.soap.client2.SistarbancMDTrfC2BAUTORIZACIONResponse;

import java.util.Optional;

import static uy.com.md.sistar.util.SistarConstants.UNKNOWN_ERR_DESC;

@Service
public class C2PService {

  private static final Logger logger = LoggerFactory.getLogger(C2PService.class);
  public static final String C2P_TRANSFER = "c2pTransfer";
  private final ModelMapper modelMapper;
  private final SistarServiceProperties config;
  private final C2BService c2BService;
  private final ServiceUtils serviceUtils;
  private final SistarClient sistarClientC2BMultiproc;

  public C2PService(ModelMapper modelMapper,
                    SistarServiceProperties config,
                    C2BService c2BService,
                    ServiceUtils serviceUtils,
                    @Qualifier("SistarClientC2BMultiproc") SistarClient sistarClientC2BMultiproc
  ){
    this.modelMapper = modelMapper;
    this.config = config;
    this.c2BService = c2BService;
    this.serviceUtils = serviceUtils;
    this.sistarClientC2BMultiproc = sistarClientC2BMultiproc;
  }

  @CircuitBreaker(name = C2P_TRANSFER, fallbackMethod = "transferFallback")
  public InternalResponseDto<TransferResponseDto> transfer(TransferRequestDto requestDto) {
    SistarbancMDTrfC2BAUTORIZACIONResponse responseSOAP = null;

    if(requestDto.getSucursal() == null){
      requestDto.setSucursal(config.getDefault_branch_office());
    }
    SistarbancMDTrfC2BAUTORIZACION requestSoap = modelMapper.map(requestDto, SistarbancMDTrfC2BAUTORIZACION.class);

    // Request authorization
    responseSOAP = sistarClientC2BMultiproc.autorizarTransferC2B(requestSoap);
    TransferResponseDto mappedResp = modelMapper.map(responseSOAP, TransferResponseDto.class);
    String code = "c2p.error";
    String desc = UNKNOWN_ERR_DESC;

    if(Boolean.TRUE.equals(mappedResp.getSuccess())) {
      // Request confirmation
      TransferUpdateRequestDto confirmRequest = new TransferUpdateRequestDto();
      confirmRequest.setCuenta(requestDto.getOrdenante().getCuenta().toString());
      confirmRequest.setRrn(requestDto.getRrn());

      InternalResponseDto<TransferResponseDto> confirmResult = c2BService.confirm(confirmRequest);
      if(Boolean.TRUE.equals(confirmResult.getRespuesta().getSuccess())){
        return confirmResult;
      }else{
        Optional<MensajeDetail> msg = confirmResult.getMensajes().stream().findFirst();
        code = "c2p.confirmation.error" + (msg.isPresent() ? "." + msg.get().getCodigo(): "");
        desc = msg.isPresent() ? msg.get().getMensaje(): UNKNOWN_ERR_DESC;
      }
    }else{
      if(responseSOAP.getWsmidineroautorizaciontarjetaresult() != null){
        code = String.valueOf(responseSOAP.getWsmidineroautorizaciontarjetaresult().getErrorCode());
        code = "c2p.authorization.error" + (StringUtils.isBlank(code) ? "": ("." + code));
        desc = responseSOAP.getWsmidineroautorizaciontarjetaresult().getErrorDescription();
      }
    }

    throw new SistarbancOperationException(code, desc);
  }

  private InternalResponseDto<TransferResponseDto> transferFallback(TransferRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }
}