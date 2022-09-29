package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.apache.commons.lang.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;
import uy.com.md.sistar.dto.IdentificationDto;
import uy.com.md.sistar.dto.in.CreditRequestDto;
import uy.com.md.sistar.dto.out.CreditResponseDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.*;
import uy.com.md.sistar.util.SistarMappingUtils;

import static uy.com.md.mensajes.enumerated.Mensajes.MD_SISTARBANC_ERROR;
import static uy.com.md.sistar.util.SistarConstants.ITC_RECHARGE_AUTHORIZATION;

@Service
public class CardCreditService {
  public static final String RECARGA_XCTA = "recargaXCta";
  public static final String RECARGA_XCI = "recargaXCI";
  private final ModelMapper modelMapper;
  private final SistarServiceProperties config;
  private final SistarClient sistarClientWSMidinero;
  private final MensajeService mensajeService;
  private static final Logger logger = LoggerFactory.getLogger(CardCreditService.class);
  private final ServiceUtils serviceUtils;
  private final ProductsQueryService productQueryService;
  private final SistarMappingUtils mappingUtils;

  public CardCreditService(ModelMapper modelMapper,
                           SistarMappingUtils mappingUtils,
                           ProductsQueryService productsQueryService,
                           SistarServiceProperties config,
                           @Qualifier("SistarClientWSMidinero") SistarClient sistarClientWSMidinero,
                           MensajeService mensajeService,
                           ServiceUtils serviceUtils
  ){
    this.modelMapper = modelMapper;
    this.mappingUtils = mappingUtils;
    this.config = config;
    this.sistarClientWSMidinero = sistarClientWSMidinero;
    this.mensajeService = mensajeService;
    this.serviceUtils = serviceUtils;
    this.productQueryService = productsQueryService;
  }

  @CircuitBreaker(name = RECARGA_XCTA, fallbackMethod = "recargaXCtaFallback")
  public InternalResponseDto<CreditResponseDto> recargaXCta(CreditRequestDto requestDto) {
    InternalResponseDto<CreditResponseDto> response;
    WSPagosAjustesResponse responseSOAP = null;
    setComprobante(requestDto);

    WSPagosAjustes requestSoap = modelMapper.map(requestDto, WSPagosAjustes.class);
    responseSOAP = sistarClientWSMidinero.recargaXCta(requestSoap);
    PagosAjustesInfo result = responseSOAP.getWSPagosAjustesResult();
    boolean success = result.getCodRetorno().isEmpty() || NumberUtils.toInt(result.getCodRetorno(), 1) == 0;

    if (success) {
      CreditResponseDto responseDto = modelMapper.map(result, CreditResponseDto.class);
      response = new InternalResponseDto<>(responseDto);
      return response;
    } else {
      throw new SistarbancOperationException(result.getCodRetorno(), result.getMensajeRetorno());
    }
  }

  private InternalResponseDto<CreditResponseDto> recargaXCtaFallback(CreditRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }

  @CircuitBreaker(name = RECARGA_XCI, fallbackMethod = "recargaXCIFallback")
  public InternalResponseDto<CreditResponseDto> recargaXCI(CreditRequestDto requestDto) {
    InternalResponseDto<CreditResponseDto> response;

    setComprobante(requestDto);
    boolean shouldGetAcctNum = shouldGetAcctNum(requestDto);
    response = sendRequest(requestDto);

    if(shouldGetAcctNum){
      try {
        if (response.getRespuesta().getCuenta() != null) {
          return response;
        }
      } catch (Exception ignore) { }
      IdentificationDto beneficiary = requestDto.getBeneficiario();
      String acct = productQueryService.getAccountNum(requestDto.getEmisor().longValue(),
          Long.parseLong(requestDto.getProducto()),
          beneficiary.getPais(),
          beneficiary.getDoc(),
          beneficiary.getTipoDoc()
      );
      if(acct != null){
        response.getRespuesta().setCuenta(acct);
      }else{
        throw new SistarbancOperationException(MD_SISTARBANC_ERROR, "No se pudo obtener el número de cuenta para el documento/producto proporcionado");
      }
    }

    return response;
  }

  private InternalResponseDto<CreditResponseDto> recargaXCIFallback(CreditRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }

  private void setComprobante(CreditRequestDto requestDto) {
    if(!config.getHandle_receipt_on_channel().isEmpty()){
      if(config.getHandle_receipt_on_channel().contains(requestDto.getCanal())){
        if(requestDto.getComprobante() == null && requestDto.getReferencia() != null){
          requestDto.setComprobante(requestDto.getReferencia());
        }
      }else{
        requestDto.setComprobante(null);
      }
    }
  }

  private boolean shouldGetAcctNum(CreditRequestDto requestDto) {
    return config.getReturn_acct_on_recharge().contains(String.format("ch-%s", requestDto.getCanal()));
  }

  private InternalResponseDto<CreditResponseDto> sendRequest(CreditRequestDto requestDto) {
    InternalResponseDto<CreditResponseDto> response;
    final Integer forceBranchOffice = mappingUtils.getBranchOffice(requestDto.getCanal(), requestDto.getProducto());
    if(forceBranchOffice != null){
      requestDto.setSucursal(forceBranchOffice.toString());
    }

    WSPagosAjustesxCI requestSoap = modelMapper.map(requestDto, WSPagosAjustesxCI.class);
    WSPagosAjustesxCIResponse responseSOAP = sistarClientWSMidinero.recargaXCI(requestSoap);

    PagosAjustesInfo result;
    result = responseSOAP.getWSPagosAjustesxCIResult();
    boolean success = result.getCodRetorno().isEmpty() || Integer.parseInt(result.getCodRetorno()) == 0;
    
    if (success) {
      CreditResponseDto data = modelMapper.map(result, CreditResponseDto.class);
      response = new InternalResponseDto<>(data);
      response.getRespuesta().setItc(ITC_RECHARGE_AUTHORIZATION);
      response.getRespuesta().setComprobante(requestDto.getComprobante());
      response.getRespuesta().setCuenta(requestDto.getCuenta());
      return response;
    }else{
      throw new SistarbancOperationException(result.getCodRetorno(), result.getMensajeRetorno());
    }
  }
}
