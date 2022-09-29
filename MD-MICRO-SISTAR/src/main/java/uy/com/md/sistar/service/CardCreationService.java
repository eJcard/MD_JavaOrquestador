package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.enumerated.Mensajes;
import uy.com.md.sistar.dto.in.NewCardRequestDto;
import uy.com.md.sistar.dto.out.NewCardResponseDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.*;
import uy.com.md.sistar.util.ExternalIdUtils;

import java.io.IOException;

import static uy.com.md.sistar.util.SistarConstants.MAX_EXTERNAL_ID_INNOMINADA;
import static uy.com.md.sistar.util.SistarConstants.MAX_TRX_ID_NOMINADA;

@Service
public class CardCreationService {

  public static final String CREATE_NOMINATED = "createNominated";
  public static final String CREATE_UNNOMINATED = "createUnnominated";
  private final SistarClient sistarClientMDF2;
  private final SistarClient sistarClientWSMidinero;
  private static final Logger logger = LoggerFactory.getLogger(CardCreditService.class);
  private final ModelMapper modelMapper;
  private final ServiceUtils serviceUtils;

  public CardCreationService(ModelMapper modelMapper,
                             ServiceUtils serviceUtils,
                             @Qualifier("SistarClientMDF2") SistarClient sistarClientMDF2,
                             @Qualifier("SistarClientWSMidinero") SistarClient sistarClientWSMidinero
  ){
    this.modelMapper = modelMapper;
    this.serviceUtils = serviceUtils;
    this.sistarClientMDF2 = sistarClientMDF2;
    this.sistarClientWSMidinero = sistarClientWSMidinero;
  }

  @CircuitBreaker(name = CREATE_NOMINATED, fallbackMethod = "createNominatedFallback")
  public InternalResponseDto<NewCardResponseDto> createNominated(NewCardRequestDto requestDto) throws IOException {
    InternalResponseDto<NewCardResponseDto> response = new InternalResponseDto<>(null, false);
    SistarbancMDF2ABMCUENTATARJETAResponse responseSOAP = null;
    validateBranchOffice(requestDto);
    validateRequestBranchOffice(requestDto);

    SistarbancMDF2ABMCUENTATARJETA requestSoap = modelMapper.map(requestDto, SistarbancMDF2ABMCUENTATARJETA.class);
    requestSoap.setTrxid(ExternalIdUtils.getIdAsLong(MAX_TRX_ID_NOMINADA));
    responseSOAP = sistarClientMDF2.solicitudProductoNominado(requestSoap);

    if (StringUtils.equals(responseSOAP.getCoderror(), "00")) {
      if (StringUtils.isBlank(responseSOAP.getExternalaccountnumber())) {
        response.addMensaje(serviceUtils.getMensajeDetail(Mensajes.MD_SISTARBANC_ERROR.ordinal(), "Falta numero de cuenta en la respuesta"));
      } else {
        response.setRespuesta(modelMapper.map(responseSOAP, NewCardResponseDto.class));
        response.setResultado(true);
      }
      return response;
    } else {
      throw new SistarbancOperationException(responseSOAP.getCoderror(), responseSOAP.getReason());
    }
  }

  private InternalResponseDto<NewCardResponseDto> createNominatedFallback(NewCardRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }

  @CircuitBreaker(name = CREATE_UNNOMINATED, fallbackMethod = "createUnnominatedFallback")
  public InternalResponseDto<NewCardResponseDto> solicitudProductoInnominado(NewCardRequestDto requestDto) {
    InternalResponseDto<NewCardResponseDto> response;
    CrearTarjetaPrepagaInnominadaResponse responseSOAP = null;
    validateBranchOffice(requestDto);

    CrearTarjetaPrepagaInnominada requestSoap = modelMapper.map(requestDto, CrearTarjetaPrepagaInnominada.class);
    requestSoap.getSolicitudPrepagaInnominada().setRequestExternalId(ExternalIdUtils.getIdAsString(MAX_EXTERNAL_ID_INNOMINADA));
    requestSoap.getSolicitudPrepagaInnominada().setRequestId(requestSoap.getSolicitudPrepagaInnominada().getRequestExternalId());
    responseSOAP = sistarClientWSMidinero.solicitudProductoInnominado(requestSoap);

    if (responseSOAP.getCrearTarjetaPrepagaInnominadaResult().getErrorCode() == 0) {
      NewCardResponseDto responseMap = modelMapper.map(responseSOAP.getCrearTarjetaPrepagaInnominadaResult(), NewCardResponseDto.class);
      response = new InternalResponseDto<>(responseMap);
      return response;
    } else {
      ResultInnominada result = responseSOAP.getCrearTarjetaPrepagaInnominadaResult();
      throw new SistarbancOperationException(result.getErrorCode(), result.getErrorDescription());
    }
  }

  private void validateBranchOffice(NewCardRequestDto requestDto){
    if(StringUtils.isBlank(requestDto.getSucursal()) || requestDto.getSucursal().equals("0")){
      throw new SistarbancOperationException("newcard.sucursal.invalid", "Debe indicar una sucursal válida.");
    }
  }

  private void validateRequestBranchOffice(NewCardRequestDto requestDto){
    if(StringUtils.isBlank(requestDto.getSucursalSolicitud()) || requestDto.getSucursalSolicitud().equals("0")){
      throw new SistarbancOperationException("newcard.sucursalSolicitud.invalid", "Debe indicar una sucursal de solicitúd válida.");
    }
  }

  private InternalResponseDto<NewCardResponseDto> createUnnominatedFallback(NewCardRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }
}
