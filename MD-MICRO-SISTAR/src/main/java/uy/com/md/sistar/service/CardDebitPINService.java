package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.common.in.PagoServiciosRequestDto;
import uy.com.md.common.out.PagoServiciosResponse;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.PagoServicioCajaPrepaga;
import uy.com.md.sistar.soap.client.PagoServicioCajaPrepagaResponse;
import uy.com.md.sistar.soap.client.PagoServicioCajaPrepagaResult;

import static uy.com.md.sistar.util.SistarConstants.UNKNOWN_ERR_CODE;

@Service
public class CardDebitPINService {

  private static final Logger logger = LoggerFactory.getLogger(CardDebitPINService.class);
  public static final String CARD_DEBIT_WITH_PIN = "cardDebitWithPIN";
  private final ModelMapper modelMapper;
  private final SistarClient sistarClientWSMidinero;
  private final ServiceUtils serviceUtils;

  public CardDebitPINService(ModelMapper modelMapper,
                             @Qualifier("SistarClientWSMidinero") SistarClient sistarClientWSMidinero,
                             ServiceUtils serviceUtils
  ){
    this.modelMapper = modelMapper;
    this.sistarClientWSMidinero = sistarClientWSMidinero;
    this.serviceUtils = serviceUtils;
  }


  @CircuitBreaker(name = CARD_DEBIT_WITH_PIN, fallbackMethod = "authorizeFallback")
  public InternalResponseDto<PagoServiciosResponse> authorize(PagoServiciosRequestDto requestDto) {
    InternalResponseDto<PagoServiciosResponse> response = new InternalResponseDto<>();
    PagoServicioCajaPrepagaResponse responseSoap = null;

    PagoServicioCajaPrepaga requestSoap =  modelMapper.map(requestDto, PagoServicioCajaPrepaga.class);
    responseSoap = sistarClientWSMidinero.solicitarPago(requestSoap);
    PagoServicioCajaPrepagaResult result = responseSoap.getPagoServicioCajaPrepagaResult();
    boolean success = result.getErrorCode() == 0;

    if(success){
      PagoServiciosResponse data = modelMapper.map(responseSoap, PagoServiciosResponse.class);
      response = new InternalResponseDto<>(data);
      return response;
    }else{
      throw new SistarbancOperationException(result.getErrorCode(), result.getErrorDescription());
    }
  }

  private InternalResponseDto<PagoServiciosResponse> authorizeFallback(PagoServiciosRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }
}
