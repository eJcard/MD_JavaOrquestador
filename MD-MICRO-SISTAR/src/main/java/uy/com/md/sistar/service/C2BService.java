package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.sistar.dto.in.TransferRequestDto;
import uy.com.md.sistar.dto.in.TransferUpdateRequestDto;
import uy.com.md.sistar.dto.out.TransferResponseDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client2.*;

@Service
public class C2BService {
  private static final Logger logger = LoggerFactory.getLogger(C2BService.class);
  public static final String C2B_AUTHORIZE = "c2bAuthorize";
  public static final String C2B_CONFIRM = "c2bConfirm";
  public static final String C2B_REVERSE = "c2bReverse";
  private final ModelMapper modelMapper;
  private final SistarClient sistarClientC2BMultiproc;
  private final ServiceUtils serviceUtils;

  public C2BService(ModelMapper modelMapper,
                    @Qualifier("SistarClientC2BMultiproc") SistarClient sistarClientC2BMultiproc,
                    ServiceUtils serviceUtils
  ){
    this.modelMapper = modelMapper;
    this.sistarClientC2BMultiproc = sistarClientC2BMultiproc;
    this.serviceUtils = serviceUtils;
  }

  @CircuitBreaker(name = C2B_AUTHORIZE, fallbackMethod = "authorizeFallback")
  public InternalResponseDto<TransferResponseDto> authorize(TransferRequestDto requestDto) {
    InternalResponseDto<TransferResponseDto> response;
    SistarbancMDTrfC2BAUTORIZACIONResponse responseSOAP = null;
    SistarbancMDTrfC2BAUTORIZACION requestSoap = modelMapper.map(requestDto, SistarbancMDTrfC2BAUTORIZACION.class);
    responseSOAP = sistarClientC2BMultiproc.autorizarTransferC2B(requestSoap);
    TransferResponseDto mappedResp = modelMapper.map(responseSOAP, TransferResponseDto.class);
    response = new InternalResponseDto<>(mappedResp);

    if(Boolean.TRUE.equals(response.getRespuesta().getSuccess())){
      return response;
    }else{
      WSMiDineroAutorizacionTarjetaResult result = responseSOAP.getWsmidineroautorizaciontarjetaresult();
      throw new SistarbancOperationException(result.getErrorCode(), result.getErrorDescription());
    }
  }

  private InternalResponseDto<TransferResponseDto> authorizeFallback(TransferRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }

  @CircuitBreaker(name = C2B_CONFIRM, fallbackMethod = "confirmFallback")
  public InternalResponseDto<TransferResponseDto> confirm(TransferUpdateRequestDto requestDto) {
    InternalResponseDto<TransferResponseDto> response;
    SistarbancMDTrfC2BCONFIRMACIONResponse responseSOAP = null;

    SistarbancMDTrfC2BCONFIRMACION requestSoap = modelMapper.map(requestDto, SistarbancMDTrfC2BCONFIRMACION.class);
    responseSOAP = sistarClientC2BMultiproc.confirmarTransferC2B(requestSoap);
    TransferResponseDto mappedResp = modelMapper.map(responseSOAP, TransferResponseDto.class);
    response = new InternalResponseDto<>(mappedResp);

    if(Boolean.TRUE.equals(response.getRespuesta().getSuccess())){
      return response;
    }else{
      WSMiDineroConfirmarAutorizacionPrepagaResult result = responseSOAP.getWsmidineroconfirmarautorizacionprepagaresult();
      throw new SistarbancOperationException(result.getErrorCode(), result.getErrorDescription());
    }
  }

  private InternalResponseDto<TransferResponseDto> confirmFallback(TransferUpdateRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }

  @CircuitBreaker(name = C2B_REVERSE, fallbackMethod = "reverseFallback")
  public InternalResponseDto<TransferResponseDto> reverse(TransferUpdateRequestDto requestDto) {
    InternalResponseDto<TransferResponseDto> response;
    SistarbancMDTrfC2BREVERSOResponse responseSOAP;

    SistarbancMDTrfC2BREVERSO requestSoap = modelMapper.map(requestDto, SistarbancMDTrfC2BREVERSO.class);
    responseSOAP = sistarClientC2BMultiproc.reversarTransferC2B(requestSoap);
    TransferResponseDto mappedResp = modelMapper.map(responseSOAP, TransferResponseDto.class);

    response = new InternalResponseDto<>(mappedResp);
    if(Boolean.TRUE.equals(response.getRespuesta().getSuccess())){
      return response;
    }else{
      WSMiDineroResult result = responseSOAP.getWsmidineroresult();
      throw new SistarbancOperationException(result.getErrorCode(), result.getErrorDescription());
    }
  }

  private InternalResponseDto<TransferResponseDto> reverseFallback(TransferUpdateRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }
}