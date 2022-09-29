package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.common.movimientos.*;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;
import uy.com.md.sistar.dto.in.TransactionInfoRequestDto;
import uy.com.md.sistar.dto.in.TransactionsListRequestDto;
import uy.com.md.sistar.dto.out.TransactionInfoResponseDto;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.*;
import uy.com.md.sistar.util.SistarMappingUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static uy.com.md.sistar.util.SistarConstants.QUERY_TRANSACTION_ERR_CODE;
import static uy.com.md.sistar.util.SistarConstants.UNKNOWN_ERR_CODE;

@Service
public class QueryTransactionsService {

  private static final Logger logger = LoggerFactory.getLogger(QueryTransactionsService.class);
  public static final String GET_TRANSACTIONS = "getTransactions";
  public static final String GET_TRANSACTION = "getTransaction";

  private final SistarClient sistarClientV2;
  private final SistarClient sistarClientWSMidinero;
  private final ModelMapper modelMapper;
  private final MensajeService mensajeService;
  private final SistarServiceProperties config;
  private final ServiceUtils serviceUtils;
  private final SistarMappingUtils mappingUtils;

  public QueryTransactionsService(ModelMapper modelMapper,
                                  MensajeService messageService,
                                  SistarServiceProperties config,
                                  ServiceUtils serviceUtils,
                                  SistarMappingUtils mappingUtils,
                                  @Qualifier("SistarClientMDV2") SistarClient sistarClientV2,
                                  @Qualifier("SistarClientWSMidinero") SistarClient sistarClientWSMidinero
  ){
    this.modelMapper = modelMapper;
    this.config = config;
    this.serviceUtils = serviceUtils;
    this.mappingUtils = mappingUtils;
    this.sistarClientV2 = sistarClientV2;
    this.sistarClientWSMidinero = sistarClientWSMidinero;
    this.mensajeService = messageService;
  }

  private InternalResponseDto<InfoDto> getErrorResponse(SistarbancMDV2MOVENPERIODOResponse responseSOAP, Exception e) {
    InternalResponseDto<InfoDto> response;
    logger.error("movimientos", e);
    response = new InternalResponseDto<>();
    response.setResultado(false);
    if (responseSOAP != null && !responseSOAP.getMovenperiodo().getMOVENPERIODOITEM().isEmpty()) {
      MOVENPERIODOITEM firstItem = Optional.of(responseSOAP.getMovenperiodo())
          .map(MOVENPERIODO::getMOVENPERIODOITEM)
          .map(movenperiodoitems -> movenperiodoitems.get(0))
          .orElse(null);
      String reason = firstItem != null ? firstItem.getREASON(): "Unknown error";
      String code = firstItem != null ? firstItem.getERRORCODI() : UNKNOWN_ERR_CODE;
      response.addMensaje(mensajeService.obtenerMensajeDetail(
          new MensajeOrigen(Origenes.SISTAR, code, reason)));
    } else {
      response.addMensaje(serviceUtils.exceptionMessage(e));
    }
    return response;
  }

  @CircuitBreaker(name = GET_TRANSACTIONS, fallbackMethod = "getTransactionsFallback")
  public InternalResponseDto<InfoDto> getTransactions(TransactionsListRequestDto requestDto) throws DatatypeConfigurationException {
    InternalResponseDto<InfoDto> response;
    SistarbancMDV2MOVENPERIODOResponse responseSOAP = null;

    // Restringe la cantidad de meses en el pasado de los que se puede consultar movimientos
    String variableLimit = "";
    if(config.getMax_months() != null){
      YearMonth month = YearMonth.now().plusMonths(config.getMax_months() * -1L);
      variableLimit = month.atDay(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    String staticLimit = config.getMovements_limit_date() != null ? config.getMovements_limit_date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")): "";

    String limit = Collections.max(Arrays.asList(staticLimit, variableLimit));

    if(!limit.isEmpty() && requestDto.getStart() != null){
      if(requestDto.getEnd().compareTo(limit) < 0){
        return new InternalResponseDto<>(new InfoDto() {{
          setJournalCode("Sistarbanc");
          setTransactions(Collections.emptyList());
        }});
      }else if(requestDto.getStart().compareTo(limit) < 0){
        requestDto.setStart(limit);
      }
    }


    SistarbancMDV2MOVENPERIODO requestSoap = modelMapper.map(requestDto, SistarbancMDV2MOVENPERIODO.class);

    responseSOAP = sistarClientV2.movimientos(requestSoap);
    MOVENPERIODOITEM firstItem = responseSOAP.getMovenperiodo().getMOVENPERIODOITEM().get(0);
    boolean success = NumberUtils.toInt(firstItem.getERRORCODI()) == 0;

    if(!success){
      throw new SistarbancOperationException(QUERY_TRANSACTION_ERR_CODE, firstItem.getREASON());
    }

    List<MOVENPERIODOITEM> responseSoapItems = responseSOAP.getMovenperiodo().getMOVENPERIODOITEM();

    boolean filtroManual = requestSoap.getDatefrom() != null && requestSoap.getDateto() != null && config.getForce_transactions_date_filter();
    if(filtroManual){
      DatatypeFactory df = DatatypeFactory.newInstance();
      requestSoap.getDateto().add(df.newDuration("P1D"));
    }
    Map<String, Boolean> inverted_amounts = config.getInvert_amounts();
    Map<String, Boolean> filter_code = config.getFilter_code();
    boolean exclude_voids = config.getExclude_voids();
    boolean exclude_void_withdraws = config.getExclude_void_withdraws();
    boolean active_filter_country = config.getActive_filter_country();
    Boolean force_merchant_id = config.getForce_merchant_id();
    boolean exclude_refund_authorizations = config.isExclude_refund_authorizations();

    List<TransaccionesDto> items = responseSOAP
        .getMovenperiodo()
        .getMOVENPERIODOITEM()
        .stream()
        .filter(m -> !exclude_voids || m.getESTADOCODI() != 3)
        .filter(m -> {
          if (filtroManual && m.getTRANSACTIONDATE() != null) {
            return m.getTRANSACTIONDATE().compare(requestSoap.getDatefrom()) >= 0 &&
                m.getTRANSACTIONDATE().compare(requestSoap.getDateto()) < 0;
          }
          return true;
        })
        .peek(m -> {
          boolean isNegative = m.getCARDHOLDERAMOUNT() < 0 || m.getSETTLEMENTAMOUNT() < 0 || m.getORIGINALAMOUNT() < 0;

          if (inverted_amounts.getOrDefault(String.format("cod-%s-scod-%s", m.getCODTX(), m.getSUBCODTX()), false) ||
              (isNegative && inverted_amounts.getOrDefault(String.format("cod-%s-scod-%s-N", m.getCODTX(), m.getSUBCODTX()), false)) ||
              inverted_amounts.getOrDefault(String.format("cod-%s-scod-all", m.getCODTX()), false) ||
              inverted_amounts.getOrDefault(String.format("stat-%s-type-%s", m.getESTADOTIPO(), m.getESTADOCODI()), false) ||
              inverted_amounts.getOrDefault(String.format("stat-%s-type-all", m.getESTADOTIPO()), false)) {
            m.setCARDHOLDERAMOUNT(m.getCARDHOLDERAMOUNT() * -1);
            m.setSETTLEMENTAMOUNT(m.getSETTLEMENTAMOUNT() * -1);
            m.setORIGINALAMOUNT(m.getORIGINALAMOUNT() * -1);
          }
        })
        .filter(i -> i.getCARDHOLDERAMOUNT() != 0.0 && NumberUtils.toInt(i.getTRANSACTIONBIN(), 0) != 0) // exclude items with empty values
        .filter(i -> !exclude_void_withdraws ||
            i.getCODTX() != 21 ||
            i.getSUBCODTX() != 22 ||
            i.getESTADOCODI() != 3
        ) // exclude voided withdraws
        // configurable exclusion filter
        .filter(m -> !filter_code.getOrDefault(String.format("cod-%s-scod-%s", m.getCODTX(), m.getSUBCODTX()), false))
        .filter(m -> !filter_code.getOrDefault(String.format("code-%s-scode-%s", m.getCODTX(), m.getSUBCODTX()), false))
        .map(m -> modelMapper.map(m, TransaccionesDto.class))
        .map(m -> {
          if (m.getComercioDescripcion() != null && m.getComercioDescripcion().length() > 1) {
            m.setComercioDescripcion(m.getComercioDescripcion().substring(0, Math.min(23, m.getComercioDescripcion().length())).trim());
          }
          return m;
        }) // truncate merchant description to 23 characters
        .map(m -> {
          if (StringUtils.isBlank(m.getComercio()) && force_merchant_id) {
            m.setComercio(m.getComercioDescripcion());
          }
          return m;
        }) // set merchant code field to merchant desctiption if ít's empty
        .map(m -> {
          if (m.getMonedaOrigen() == null && m.getMontoOrigen().equals(m.getMonto())) {
            m.setMonedaOrigen(m.getMoneda());
          }
          return m;
        }) // some times original currency is not provided, then check if original amount is equal to amount. In that casi use currency as original currency
        .filter(i -> {
          if (active_filter_country && requestDto.getCurrency() != null && !requestDto.getCurrency().equals(0)) {
            return i.getMoneda().equals(requestDto.getCurrency().toString());
          }
          return true;
        })
        .map(m -> {
          if (m.getCodigo().equals("R200.40.01")) {
            m.setComercio("");
            m.setComercioDescripcion("");
          }
          return m;
        })
        .map(m -> {
          m.setDescripcion(mappingUtils.getMovementDescription(m, m.getDescripcion()));
          return m;
        })
        .map(m -> {
          if (m.getSbCode() == 0 && m.getComercioDescripcion().equals("CONSUMO POR TRANSFEREN")) {
            m.setComercio("");
            m.setComercioDescripcion("");
            m.setCodigo("R200.40.01");
          }
          return m;
        })
        .map(m -> {
          if (m.getSbCode() == 11 && (m.getSbSubcode() == 22 || m.getSbSubcode() == 26)) {
            m.setMcc(mappingUtils.getMccByCodSubcode(m, m.getMcc()));
          }
          return m;
        })
        .map(m -> {
          if (m.getSbCode() == 0 && m.getComercioDescripcion().equals("C2C") || m.getComercioDescripcion().equals("CONSUMO C2C")) {
            m.setCodigo("T200.40.01");
          }
          return m;
        })
        .map(m -> {
          if (m.getSbCode() == 0 && m.getComercioDescripcion().equals("MIDINERO PAGO DE SERVI")) {
            m.setCodigo("200.00");
            m.setComercio("PAGO DE SERVICIOS");
            m.setComercioDescripcion("PAGO DE SERVICIOS");
          }
          return m;
        })
        .map(m -> {
          m.setComercio(mappingUtils.getMovementMerchantDescription(m, m.getComercio()));
          m.setComercioDescripcion(mappingUtils.getMovementMerchantDescription(m, m.getComercioDescripcion()));
          return m;
        })
        .map(m -> {
          if (m.getSbCode() == 35 && m.getSbSubcode() == 21) {
            m.setConcepto(new ConceptoDto() {{
              setDescripcion("Credito");
            }});
          }
          return m;
        })
        .map(m -> {
          if (m.getCodigo().equals("R200.40.01")) {
            m.setComercio(m.getDescripcion());
            m.setComercioDescripcion(m.getDescripcion());
          }
          return m;
        })
        .map(m -> {
          if (m.getCodigo().equals("R200.40.21")) {
            m.setComercio(m.getDescripcion());
            m.setComercioDescripcion(m.getDescripcion());
          }
          return m;
        })
        .map(m -> {
          if (m.getCodigo().equals("200.00") && m.getMcc().equals("9999")) {
            m.setMcc("4900");
          }
          return m;
        })
        // Excluyo devoluciones que aun no fueron aprobadas de acuerdo al valor del  flag.
        .filter(m -> !exclude_refund_authorizations || !(m.getSbCode() == 12 && m.getSbSubcode() == 0 && m.getSbStatusType() == 6))
        .filter(m -> !mappingUtils.shouldExclude(m))
        .collect(Collectors.toList());

    // Response includes error message
    if (responseSoapItems.size() == 1 && !responseSoapItems.get(0).getERRORCODI().equals("00")) {
      response = getErrorResponse(responseSOAP, null);
    } else {
      TransaccionesResponseDto responseDto = new TransaccionesResponseDto();
      InfoDto infoDto = new InfoDto();
      PaginadoDto paginado = new PaginadoDto();
      int registros = items.size();
      int length = requestDto.getLength();
      paginado.setTamanioPagina(length);
      paginado.setPaginaActual(requestDto.getOffset());
      paginado.setCantidadRegistros(registros);
      paginado.setCantidadPaginas((registros + length - 1) / length);
      infoDto.setJournalCode("Sistarbanc");
      infoDto.setTransactions(items);
      infoDto.setPaginado(paginado);

      responseDto.setSuccess(true);
      responseDto.setInfo(infoDto);
      response = new InternalResponseDto<>(infoDto);
    }
    return response;
  }

  private InternalResponseDto<InfoDto> getTransactionsFallback(TransactionsListRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }

  @CircuitBreaker(name = GET_TRANSACTION, fallbackMethod = "getTransactionFallback")
  public InternalResponseDto<TransactionInfoResponseDto> getTransaction(TransactionInfoRequestDto requestDto) {
    InternalResponseDto<TransactionInfoResponseDto> response = null;
    ConsultaPorReferencia requestSoap;

    requestSoap = modelMapper.map(requestDto, ConsultaPorReferencia.class);
    ConsultaPorReferenciaResponse responseSoap = sistarClientWSMidinero.obtenerInfoTransaccion(requestSoap);
    if(responseSoap.getConsultaPorReferenciaResult().getErrorCode() == 0){
      TransactionInfoResponseDto mappedResponse = modelMapper.map(responseSoap, TransactionInfoResponseDto.class);
      response = new InternalResponseDto<>(mappedResponse);
    }else{
      response = new InternalResponseDto<>(new TransactionInfoResponseDto());
      response.getRespuesta().setSuccess(false);
      response.setResultado(false);
      ConsultaPorReferenciaResult result = responseSoap.getConsultaPorReferenciaResult();
      String[] errorMsgLines = result.getErrorDescription().split("\n");
      response.addMensaje(serviceUtils.getMensajeDetail(result.getErrorCode(), errorMsgLines.length > 0 ? errorMsgLines[0]: ""));
      response.setResultado(response.getRespuesta().isSuccess());
    }

    return response;
  }

  private InternalResponseDto<TransactionInfoResponseDto> getTransactionFallback(TransactionInfoRequestDto requestDto, Exception ex) {
    return serviceUtils.crackResponse(requestDto, ex);
  }
}
