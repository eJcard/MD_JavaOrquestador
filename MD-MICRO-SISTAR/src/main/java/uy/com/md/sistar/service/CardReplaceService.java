package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.sistar.dto.in.ReemplazoRequestDto;
import uy.com.md.sistar.dto.out.CardsResponseDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.*;

@Service
public class CardReplaceService {

  private static final Logger logger = LoggerFactory.getLogger(CardReplaceService.class);
  public static final String REPLACE_UNNAMED_BY_ACCOUNT = "replaceUnnamedByAccount";
  public static final String REPLACE_UNNAMED_BY_DOCUMENT = "replaceUnnamedByDocument";
  private static final String REPLACE_UNNAMED = "replaceUnnamed";

  private final ModelMapper modelMapper;
  private final SistarClient sistarClientWSMidinero;
  private final ServiceUtils serviceUtils;
  private final SistarServiceProperties config;

  public CardReplaceService(ModelMapper modelMapper,
                            @Qualifier("SistarClientWSMidinero") SistarClient sistarClientWSMidinero,
                            ServiceUtils serviceUtils,
                            SistarServiceProperties config
  ){
    this.modelMapper = modelMapper;
    this.sistarClientWSMidinero = sistarClientWSMidinero;
    this.serviceUtils = serviceUtils;
    this.config = config;
  }

  @CircuitBreaker(name = REPLACE_UNNAMED, fallbackMethod = "fallback")
  public InternalResponseDto<CardsResponseDto> reemplazoProductoInnominado(ReemplazoRequestDto requestDto) {
    if(Boolean.TRUE.equals(config.getReplace_card_by_account_enabled()) && requestDto.getCuenta() != null && requestDto.getCuenta() != 0){
      return reemplazoPorCuenta(requestDto);
    }else{
      return reemplazoPorDocumento(requestDto);
    }
  }

  private InternalResponseDto<CardsResponseDto> fallback(ReemplazoRequestDto requestDto, Exception ex){
    return serviceUtils.crackResponse(requestDto, ex);
  }

  private InternalResponseDto<CardsResponseDto> reemplazoPorCuenta(ReemplazoRequestDto requestDto) {
    InternalResponseDto<CardsResponseDto> response;
    ReemplazoTarjetaPrepagaInnominadaResponse responseSOAP = null;
    try {
      ReemplazoTarjetaPrepagaInnominada requestSoap = modelMapper.map(requestDto, ReemplazoTarjetaPrepagaInnominada.class);

      responseSOAP = sistarClientWSMidinero.reemplazoProductoInnominadoXCuenta(requestSoap);
      if (responseSOAP.getReemplazoTarjetaPrepagaInnominadaResult().getErrorCode() == 0) {
        CardsResponseDto r = new CardsResponseDto();
        r.setSuccess(true);
        r.setRealAccount(String.valueOf(requestDto.getCuenta()));
        response = new InternalResponseDto<>(r);
      } else {
        CardsResponseDto r = new CardsResponseDto();
        r.setSuccess(false);
        r.setErrors(responseSOAP.getReemplazoTarjetaPrepagaInnominadaResult().getErrorDescription());
        response = new InternalResponseDto<>(r);
        response.setResultado(false);
        response.addMensaje(serviceUtils.getMensajeDetail(responseSOAP.getReemplazoTarjetaPrepagaInnominadaResult().getErrorCode(),
            responseSOAP.getReemplazoTarjetaPrepagaInnominadaResult().getErrorDescription()));
      }
    } catch (Exception e) {
      logger.error("reemplazoProductoInnominado", e);
      CardsResponseDto r = new CardsResponseDto();
      r.setSuccess(false);
      response = new InternalResponseDto<>(r);
      response.setResultado(false);
      if (responseSOAP != null) {
        response.addMensaje(serviceUtils.getMensajeDetail(responseSOAP.getReemplazoTarjetaPrepagaInnominadaResult().getErrorCode(),
            responseSOAP.getReemplazoTarjetaPrepagaInnominadaResult().getErrorDescription()));
      } else {
        response.addMensaje(serviceUtils.exceptionMessage(e));
      }
    }
    return response;
  }

  private InternalResponseDto<CardsResponseDto> reemplazoPorDocumento(ReemplazoRequestDto requestDto) {
    InternalResponseDto<CardsResponseDto> response;
    ReemplazoTarjetaPrepagaInnominadaXCiResponse responseSOAP = null;
    if(requestDto.getEmisor() == null){
      throw new IllegalArgumentException("Sebe indicar codigo de emisor.");
    }
    ReemplazoTarjetaPrepagaInnominadaXCi requestSoap = modelMapper.map(requestDto, ReemplazoTarjetaPrepagaInnominadaXCi.class);
    responseSOAP = sistarClientWSMidinero.reemplazoProductoInnominadoXCI(requestSoap);
    Result result = responseSOAP.getReemplazoTarjetaPrepagaInnominadaXCiResult();
    boolean success = result.getErrorCode() == 0;

    if (success) {
      CardsResponseDto r = new CardsResponseDto();
      r.setSuccess(true);
      r.setRealAccount(String.valueOf(requestDto.getCuenta()));
      return new InternalResponseDto<>(r);
    } else {
      throw new SistarbancOperationException(result.getErrorCode(), result.getErrorDescription());
    }
  }
}
