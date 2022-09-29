package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
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

import java.util.Objects;

@Service
public class CardDebitService {

  public static final String DEBIT_AUTHORIZE = "debitAuthorize";
  private final SistarServiceProperties config;
  private final ModelMapper modelMapper;
  private final SistarClient sistarClientC2BMultiproc;
  private final ServiceUtils serviceUtils;

  public CardDebitService(ModelMapper modelMapper,
                          SistarServiceProperties config,
                          @Qualifier("SistarClientC2BMultiproc") SistarClient sistarClientC2BMultiproc,
                          ServiceUtils serviceUtils
  ){
    this.modelMapper = modelMapper;
    this.config = config;
    this.sistarClientC2BMultiproc = sistarClientC2BMultiproc;
    this.serviceUtils = serviceUtils;
  }

  @CircuitBreaker(name = DEBIT_AUTHORIZE, fallbackMethod = "authorizeFallback")
  public InternalResponseDto<TransferResponseDto> authorize(TransferRequestDto requestDto) {
    SistarbancMDTrfC2BAUTORIZACIONResponse responseSOAP;
    if(requestDto.getSucursal() == null){
      requestDto.setSucursal(config.getDefault_branch_office());
    }
    SistarbancMDTrfC2BAUTORIZACION requestSoap = modelMapper.map(requestDto, SistarbancMDTrfC2BAUTORIZACION.class);
    responseSOAP = sistarClientC2BMultiproc.autorizarTransferC2B(requestSoap);
    WSMiDineroAutorizacionTarjetaResult result = responseSOAP.getWsmidineroautorizaciontarjetaresult();
    boolean success = result.getErrorCode() == 0;

    if(success){
      TransferUpdateRequestDto confirmRequest = new TransferUpdateRequestDto();
      confirmRequest.setCuenta(requestDto.getOrdenante().getCuenta().toString());
      confirmRequest.setRrn(requestDto.getRrn());

      return confirm(confirmRequest);
    }else{
      throw new SistarbancOperationException(result.getErrorCode(), result.getErrorDescription());
    }
  }

  private InternalResponseDto<TransferResponseDto> authorizeFallback(TransferRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }

  public InternalResponseDto<TransferResponseDto> confirm(TransferUpdateRequestDto requestDto) {
    InternalResponseDto<TransferResponseDto> response;
    SistarbancMDTrfC2BCONFIRMACIONResponse responseSOAP;

    SistarbancMDTrfC2BCONFIRMACION requestSoap = modelMapper.map(requestDto, SistarbancMDTrfC2BCONFIRMACION.class);
    responseSOAP = sistarClientC2BMultiproc.confirmarTransferC2B(requestSoap);
    WSMiDineroConfirmarAutorizacionPrepagaResult result = responseSOAP.getWsmidineroconfirmarautorizacionprepagaresult();
    TransferResponseDto mappedResp = modelMapper.map(responseSOAP, TransferResponseDto.class);
    response = new InternalResponseDto<>(mappedResp);

    if(Boolean.TRUE.equals(response.getRespuesta().getSuccess())){
      return response;
    }else{
      throw new SistarbancOperationException(result.getErrorCode(), result.getErrorDescription());
    }
  }
}
