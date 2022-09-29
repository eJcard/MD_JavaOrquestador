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
import uy.com.md.sistar.soap.client.RenovacionTarjetaPrepagaInnominadaXCi;
import uy.com.md.sistar.soap.client.RenovacionTarjetaPrepagaInnominadaXCiResponse;
import uy.com.md.sistar.soap.client.Result;

@Service
public class CardRenewalService {

  private static final Logger logger = LoggerFactory.getLogger(CardRenewalService.class);
  public static final String RENEW_UNNAMED = "renewUnnamed";
  private final ModelMapper modelMapper;
  private final SistarClient sistarClientWSMidinero;
  private final ServiceUtils serviceUtils;

  public CardRenewalService(ModelMapper modelmapper,
                            ServiceUtils serviceUtils,
                            @Qualifier("SistarClientWSMidinero") SistarClient sistarClientWSMidinero
  ){
    this.serviceUtils = serviceUtils;
    this.sistarClientWSMidinero = sistarClientWSMidinero;
    this.modelMapper = modelmapper;
  }

  @CircuitBreaker(name = RENEW_UNNAMED, fallbackMethod = "fallback")
  public InternalResponseDto<CardsResponseDto> renovacionProductoInnominado(ReemplazoRequestDto requestDto) {
    return renovacionPorDocumento(requestDto);
  }

  private InternalResponseDto<CardsResponseDto> renovacionPorDocumento(ReemplazoRequestDto requestDto) {
    InternalResponseDto<CardsResponseDto> response;
    RenovacionTarjetaPrepagaInnominadaXCiResponse responseSOAP = null;

    if(requestDto.getEmisor() == null){
      throw new IllegalArgumentException("Se debe indicar código de emisor.");
    }
    RenovacionTarjetaPrepagaInnominadaXCi requestSoap = modelMapper.map(requestDto, RenovacionTarjetaPrepagaInnominadaXCi.class);

    responseSOAP = sistarClientWSMidinero.renovacionProductoInnominadoXCI(requestSoap);
    Result result = responseSOAP.getRenovacionTarjetaPrepagaInnominadaXCiResult();
    boolean success = result.getErrorCode() == 0;

    if (success) {
      CardsResponseDto data = new CardsResponseDto();
      data.setSuccess(true);
      data.setRealAccount(String.valueOf(requestDto.getCuenta()));
      return new InternalResponseDto<>(data);
    } else {
      throw new SistarbancOperationException(result.getErrorCode(), result.getErrorDescription());
    }
  }

  private InternalResponseDto<CardsResponseDto> fallback (ReemplazoRequestDto requestDto, Exception ex){
    return serviceUtils.crackResponse(requestDto, ex);
  }
}
