package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.sistar.dto.in.ReverseRequestDto;
import uy.com.md.sistar.dto.in.TransferRequestDto;
import uy.com.md.sistar.dto.out.ReverseResponseDto;
import uy.com.md.sistar.dto.out.TransferResponseDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.*;

@Service
public class C2CService {

  private static final Logger logger = LoggerFactory.getLogger(C2CService.class);
  public static final String C2C_TRANSFER = "c2cTransfer";
  public static final String C2C_REVERSE = "c2cReverse";
  private final ModelMapper modelMapper;
  private final SistarClient sistarClientWSMidinero;
  private final ServiceUtils serviceUtils;

  public C2CService(ModelMapper modelMapper,
                    @Qualifier("SistarClientWSMidinero") SistarClient sistarClientWSMidinero,
                    ServiceUtils serviceUtils
  ){
    this.modelMapper = modelMapper;
    this.sistarClientWSMidinero = sistarClientWSMidinero;
    this.serviceUtils = serviceUtils;
  }

  @CircuitBreaker(name = C2C_TRANSFER, fallbackMethod = "transferFallback")
  public InternalResponseDto<TransferResponseDto> transfer(TransferRequestDto requestDto) {
    InternalResponseDto<TransferResponseDto> response;
    TransferenciaC2CResponse responseSOAP = null;
    TransferenciaC2C requestSoap = modelMapper.map(requestDto, TransferenciaC2C.class);
    responseSOAP = sistarClientWSMidinero.transferC2C(requestSoap);
    TransferResponseDto mappedResp = modelMapper.map(responseSOAP.getTransferenciaC2CResult(), TransferResponseDto.class);
    response = new InternalResponseDto<>(mappedResp);

    if(Boolean.TRUE.equals(response.getRespuesta().getSuccess())){
      return response;
    }
    else{
      TransferenciaC2CResult result = responseSOAP.getTransferenciaC2CResult();
      throw new SistarbancOperationException(result.getErrorCode(), result.getErrorDescription());
    }
  }

  private InternalResponseDto<TransferResponseDto> transferFallback(TransferRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }

  @CircuitBreaker(name = C2C_REVERSE, fallbackMethod = "reverseFallback")
  public InternalResponseDto<ReverseResponseDto> reverse(ReverseRequestDto requestDto) {
    InternalResponseDto<ReverseResponseDto> response;
    Result responseSOAP = null;

    RevertirTransferenciaC2C requestSoap = modelMapper.map(requestDto, RevertirTransferenciaC2C.class);
    responseSOAP = sistarClientWSMidinero.reversarTransferC2C(requestSoap).getRevertirTransferenciaC2CResult();
    ReverseResponseDto mappedResp = modelMapper.map(responseSOAP, ReverseResponseDto.class);
    mappedResp.setReversalRrn(requestDto.getToReverseRrn());
    response = new InternalResponseDto<>(mappedResp);

    if(Boolean.TRUE.equals(response.getRespuesta().getSuccess())){
      return response;
    }else{
      throw new SistarbancOperationException(responseSOAP.getErrorCode(), responseSOAP.getErrorDescription());
    }
  }
  
  private InternalResponseDto<ReverseResponseDto> reverseFallback(ReverseRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }
}
