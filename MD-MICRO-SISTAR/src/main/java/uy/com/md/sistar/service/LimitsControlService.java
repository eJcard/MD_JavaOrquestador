package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.sistar.dto.in.AccountVpsRequestDto;
import uy.com.md.sistar.dto.out.AccountVPSResponseDto;
import uy.com.md.sistar.dto.out.ExceptionFraude;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.*;
import uy.com.md.sistar.util.SistarMappingUtils;

import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LimitsControlService {

  public static final String UPDATE_LIMIT = "updateLimit";
  private final ModelMapper modelMapper;
  private final SistarClient sistarClientWSMidinero;
  private final ServiceUtils serviceUtils;
  private final SistarMappingUtils mappingUtils;
  private final SistarServiceProperties config;

  public LimitsControlService(ModelMapper modelMapper,
                              ServiceUtils serviceUtils,
                              SistarMappingUtils mappingUtils,
                              SistarServiceProperties config,
                              @Qualifier("SistarClientWSMidinero") SistarClient sistarClientWSMidinero
  ){
    this.modelMapper = modelMapper;
    this.serviceUtils = serviceUtils;
    this.config = config;
    this.sistarClientWSMidinero = sistarClientWSMidinero;
    this.mappingUtils = mappingUtils;
  }

  @CircuitBreaker(name = UPDATE_LIMIT, fallbackMethod = "fallback")
  public InternalResponseDto<AccountVPSResponseDto> updateLimit(AccountVpsRequestDto requestDto) {
    InternalResponseDto<AccountVPSResponseDto> response;
    ExceptionFraudeModiResponse responseSOAP;
    InternalResponseDto<AccountVPSResponseDto> queryResult;

    queryResult = consultaVps(requestDto);

    List<ExceptionFraude> currentExceptions = queryResult.getRespuesta().getInfo().getExceptionFraude();

    // Si no se indica un id de excepcion, se consulta la excepcin activa para la moneda indicada
    if(requestDto.getId() == null){
      // Busco excepción para la moneda
      Optional<ExceptionFraude> currentException = currentExceptions
          .stream()
          .filter(e -> e.getCurrency().toString().equals(requestDto.getMoneda().toString()))
          .findFirst();

      if(currentException.isPresent()){ // Ya existe excepción de control de límites para la moneda dada

        // seteo identificador de la excepción de seguridad que se envía a SB en la solicitud de cambio de estado
        requestDto.setId(currentException.get().getId());

        // Si el llamador está solicitando deshabilitar el control de límites para la moneda, devuelvo éxito, porque ya existe la excepción.
        // En caso contrario, más abajo solicito eliminar la excepción de esa manera se re-activa el control de límites.
        if(Boolean.TRUE.equals(requestDto.getDisable())){
          AccountVPSResponseDto respDto = new AccountVPSResponseDto();
          respDto.setSuccess(true);
          respDto.getInfo().getExceptionFraude().addAll(currentExceptions);

          return new InternalResponseDto<>(respDto);
        }
      }else{ // No existe actualmente una excepción de control de límites para la moneda dada

        // Si se solicita habilitar el control de límites, devuelvo éxito, ya que no existe una excepción activa.
        if(Boolean.TRUE.equals(requestDto.getEnable())){
          AccountVPSResponseDto respDto = new AccountVPSResponseDto();
          respDto.setSuccess(true);
          respDto.getInfo().getExceptionFraude().addAll(currentExceptions);

          return new InternalResponseDto<>(respDto);
        }else{ // Se está solicitando deshabilitar el control de limites, o sea, dar de alta una nueva excepción

          // Si no se indica fecha de inicio y fin, los seteo de manera que el rango sea de una hora, desde la hora actual
          if(StringUtils.isBlank(requestDto.getInicio())){
            LocalDateTime now = LocalDateTime.now();
            ZoneId zoneId = ZoneId.of(mappingUtils.getConfig().getLocal_timezone());
            requestDto.setInicio(ZonedDateTime.of(now, zoneId).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            requestDto.setFin(ZonedDateTime.of(now.plusHours(1), zoneId).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
          }

          // Se solicita el alta de una excepción contra la API SB
          InternalResponseDto<AccountVPSResponseDto> altaResult = altaVps(requestDto);
          if(Boolean.TRUE.equals(altaResult.getRespuesta().getSuccess())){
            altaResult.getRespuesta().getInfo().getExceptionFraude().addAll(currentExceptions);
            ExceptionFraude exceptionFraude = new ExceptionFraude();
            exceptionFraude.setId(altaResult.getRespuesta().getInfo().getId());
            exceptionFraude.setFrom(requestDto.getInicio().replace("T", " "));
            exceptionFraude.setTo(requestDto.getFin().replace("T", " "));
            exceptionFraude.setCurrency(requestDto.getMoneda().longValue());
            altaResult.getRespuesta().getInfo().getExceptionFraude().add(exceptionFraude);
          }
          return altaResult;
        }
      }
    }

    // Solicito a API SB cambio de estado de una excepción.
    ExceptionFraudeModi requestSoap = modelMapper.map(requestDto, ExceptionFraudeModi.class);
    responseSOAP = sistarClientWSMidinero.cambioEstadoVps(requestSoap);
    boolean updateSuccessful = responseSOAP.getExceptionFraudeModiResult().getErrorCode() == 0;

    if(updateSuccessful){
      AccountVPSResponseDto mappedResponse = modelMapper.map(responseSOAP.getExceptionFraudeModiResult(), AccountVPSResponseDto.class);
      response = new InternalResponseDto<>(mappedResponse);
      response.getRespuesta().getInfo().getExceptionFraude().addAll(
          currentExceptions.stream()
              .filter(e -> !Objects.equals(e.getId(), requestDto.getId()))
              .collect(Collectors.toList())
      );
      return response;
    }else{
      Result result = responseSOAP.getExceptionFraudeModiResult();
      throw new SistarbancOperationException(result.getErrorCode(), result.getErrorDescription());
    }
  }

  @SneakyThrows
  public InternalResponseDto<AccountVPSResponseDto> altaVps(AccountVpsRequestDto requestDto) {
    ExceptionFraudeAltaResponse responseSOAP;

    ExceptionFraudeAlta requestSoap = modelMapper.map(requestDto, ExceptionFraudeAlta.class);
    Integer offset = config.getLimit_control_start_offset_minutes();
    requestSoap.getExceptionFraudeAlta().getFechaDesde().add(
        DatatypeFactory.newInstance().newDuration(String.format("%sPT%sM", offset < 0 ? "-": "", Math.abs(offset)))
    );
    if(requestSoap.getExceptionFraudeAlta().getMoneCodi() == 0){
      requestSoap.getExceptionFraudeAlta().setMoneCodi(requestDto.getMoneda());
    }
    responseSOAP = sistarClientWSMidinero.altaVps(requestSoap);
    if(responseSOAP.getExceptionFraudeAltaResult().getErrorCode() == 0){
      AccountVPSResponseDto mappedResponse = modelMapper.map(responseSOAP.getExceptionFraudeAltaResult(), AccountVPSResponseDto.class);
      return new InternalResponseDto<>(mappedResponse);
    }else{
      ExcepcionFraudeAltaResult result = responseSOAP.getExceptionFraudeAltaResult();
      throw new SistarbancOperationException(result.getErrorCode(), result.getErrorDescription());
    }
  }

  public InternalResponseDto<AccountVPSResponseDto> consultaVps(AccountVpsRequestDto requestDto) {
    InternalResponseDto<AccountVPSResponseDto> response;
    ExceptionFraudeConsulResponse responseSOAP;

    ExceptionFraudeConsul requestSoap = modelMapper.map(requestDto, ExceptionFraudeConsul.class);
    responseSOAP = sistarClientWSMidinero.estadoVps(requestSoap);
    AccountVPSResponseDto mappedResponse = modelMapper.map(responseSOAP.getExceptionFraudeConsulResult(), AccountVPSResponseDto.class);
    response = new InternalResponseDto<>(mappedResponse);
    response.setResultado(mappedResponse.getSuccess());
    if(requestDto.getMoneda() != null){
      response.getRespuesta().getInfo().getExceptionFraude().removeIf(e -> e.getCurrency().intValue() != requestDto.getMoneda());
    }
    return response;
  }

  private InternalResponseDto<AccountVPSResponseDto> fallback(AccountVpsRequestDto requestDto, Exception ex){
    return serviceUtils.crackResponse(requestDto, ex);
  }
}
