package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.sistar.dto.in.CardStatusUpdateDto;
import uy.com.md.sistar.dto.out.CardsResponseDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.*;

import java.util.ArrayList;

@Service
public class CardUpdateService {

  private static final Logger logger = LoggerFactory.getLogger(CardUpdateService.class);
  public static final String UPDATE_CARD_STATUS = "updateCardStatus";
  private final ModelMapper modelMapper;
  private final SistarServiceProperties sistarServiceProperties;
  private final SistarClient sistarClientUpdCardStatus;
  private final SistarClient sistarClientMDF2;
  private final ServiceUtils serviceUtils;

  public CardUpdateService(ModelMapper modelMapper,
                           SistarServiceProperties sistarServiceProperties,
                           @Qualifier("SistarClientUpdCardStatus") SistarClient sistarClientUpdCardStatus,
                           @Qualifier("SistarClientMDF2")SistarClient sistarClientMDF2,
                           ServiceUtils serviceUtils

  ){
    this.modelMapper = modelMapper;
    this.sistarServiceProperties = sistarServiceProperties;
    this.sistarClientUpdCardStatus = sistarClientUpdCardStatus;
    this.sistarClientMDF2 = sistarClientMDF2;
    this.serviceUtils = serviceUtils;
  }

  @CircuitBreaker(name = UPDATE_CARD_STATUS, fallbackMethod = "fallback")
  public InternalResponseDto<CardsResponseDto> actualizarEstadoTarjeta(CardStatusUpdateDto requestDto) {
    CardsResponseDto mappedResp;
    boolean success;

    if(sistarServiceProperties.isUse_block_card_alt_method()){
      WSCambioEstadoCuentaExecuteResponse responseSOAP;
      WSCambioEstadoCuentaExecute requestSoap = modelMapper.map(requestDto, WSCambioEstadoCuentaExecute.class);
      responseSOAP = sistarClientUpdCardStatus.cambiarEstadoTarjeta(requestSoap);
      mappedResp = modelMapper.map(responseSOAP, CardsResponseDto.class);
      OutCambioEstado result = responseSOAP.getOutcambioestado();
      success = NumberUtils.toInt(result.getCoderror(), -1) == 0;

      if(success){
        return new InternalResponseDto<>(mappedResp);
      }else{
        throw new SistarbancOperationException(result.getCoderror(), result.getReason());
      }

    }else{
      SistarbancMDF2CAMBIOESTADOTARJETAResponse responseSOAP;
      SistarbancMDF2CAMBIOESTADOTARJETA requestSoap = modelMapper.map(requestDto, SistarbancMDF2CAMBIOESTADOTARJETA.class);
      responseSOAP = sistarClientMDF2.cambiarEstadoTarjeta(requestSoap);
      mappedResp = modelMapper.map(responseSOAP, CardsResponseDto.class);
      success = NumberUtils.toInt(responseSOAP.getCoderror(), -1) == 0;

      if(success){
        return new InternalResponseDto<>(mappedResp);
      }else{
        throw new SistarbancOperationException(responseSOAP.getCoderror(), responseSOAP.getReason());
      }
    }
  }

  private InternalResponseDto<CardsResponseDto> fallback(CardStatusUpdateDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }
}
