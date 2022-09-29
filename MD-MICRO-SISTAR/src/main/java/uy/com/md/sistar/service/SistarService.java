package uy.com.md.sistar.service;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uy.com.md.common.in.PagoServiciosRequestDto;
import uy.com.md.common.movimientos.*;
import uy.com.md.common.out.PagoServiciosResponse;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeDetail;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.enumerated.Mensajes;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;
import uy.com.md.sistar.dto.IdentificationDto;
import uy.com.md.sistar.dto.TarjetasxCIResponse;
import uy.com.md.sistar.dto.TarjetasxCIResponse.TarjetaCIBanco;
import uy.com.md.sistar.dto.in.*;
import uy.com.md.sistar.dto.out.*;
import uy.com.md.sistar.dto.out.BalanceResponseDto.Info;
import uy.com.md.sistar.soap.SistarClient;
import uy.com.md.sistar.soap.client.*;
import uy.com.md.sistar.soap.client2.*;
import uy.com.md.sistar.util.ExternalIdUtils;
import uy.com.md.sistar.util.SistarConstants;
import uy.com.md.sistar.util.SistarMappingUtils;

import javax.xml.datatype.DatatypeFactory;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uy.com.md.sistar.util.SistarConstants.MAX_EXTERNAL_ID_INNOMINADA;
import static uy.com.md.sistar.util.SistarConstants.MAX_TRX_ID_NOMINADA;

@Deprecated
@Service
public class SistarService {

   @Autowired
   SistarMappingUtils mappingUtils;

   @Autowired
   SistarServiceProperties config;

   private static final Logger logger = LoggerFactory.getLogger(SistarService.class);

   private final ModelMapper modelMapper;

   private final SistarClient sistarClientMDF2;

   private final SistarClient sistarClientMDF25;

   private final SistarClient sistarTransactionClient;

   private final SistarClient sistarClient3;

   private final SistarClient sistarClientV2;

   private final SistarClient sistarClientC2BMultiproc;

   private final SistarClient sistarClientUpdCardStatus;

   private final MensajeService mensajeService;

   private final SistarServiceProperties sistarServiceProperties;


   public SistarServiceProperties getSistarServiceProperties() {
      return sistarServiceProperties;
   }

   public SistarService(ModelMapper modelMapper,
      @Qualifier("SistarClientMDF2")SistarClient sistarClientMDF2, //
      @Qualifier("SistarClientMDF25")SistarClient sistarClientMDF25, //
      @Qualifier("SistarClientWSMidinero") SistarClient sistarClientWSMidinero, //
      @Qualifier("SistarClientWSMDCustom") SistarClient sistarClientWSMDCustom, //
      @Qualifier("SistarClientMDV2") SistarClient sistarClientMDV2, //
      @Qualifier("SistarClientC2BMultiproc") SistarClient sistarClientC2BMultiproc, //
      @Qualifier("SistarClientUpdCardStatus") SistarClient sistarClientUpdCardStatus, //
      MensajeService mensajeService,
      SistarServiceProperties sistarServiceProperties
   ) {
      this.modelMapper = modelMapper;
      this.sistarClientMDF2 = sistarClientMDF2;
      this.sistarClientMDF25 = sistarClientMDF25;
      this.sistarClient3 = sistarClientWSMDCustom;
      this.sistarTransactionClient = sistarClientWSMidinero;
      this.sistarClientV2 = sistarClientMDV2;
      this.sistarClientC2BMultiproc = sistarClientC2BMultiproc;
      this.sistarClientUpdCardStatus = sistarClientUpdCardStatus;
      this.mensajeService = mensajeService;
      this.sistarServiceProperties = sistarServiceProperties;
   }

   public InternalResponseDto<NewCardResponseDto> solicitudProductoNominado(NewCardRequestDto requestDto) {
      InternalResponseDto<NewCardResponseDto> response = new InternalResponseDto<>(null, false);
      SistarbancMDF2ABMCUENTATARJETAResponse responseSOAP = null;

      if(StringUtils.isBlank(requestDto.getSucursal()) || requestDto.getSucursal().equals("0")){
         response = new InternalResponseDto<>();
         response.setResultado(false);
         response.addMensaje(getMensajeDetail("newcard.brach_office.invalid", "Debe indicar una sucursal válida."));
         return response;
      }

      if(StringUtils.isBlank(requestDto.getSucursalSolicitud()) || requestDto.getSucursalSolicitud().equals("0")){
         response = new InternalResponseDto<>();
         response.setResultado(false);
         response.addMensaje(getMensajeDetail("newcard.brach_office.invalid", "Debe indicar una sucursal válida."));
         return response;
      }

      try {
         SistarbancMDF2ABMCUENTATARJETA requestSoap = modelMapper.map(requestDto, SistarbancMDF2ABMCUENTATARJETA.class);
         requestSoap.setTrxid(ExternalIdUtils.getIdAsLong(MAX_TRX_ID_NOMINADA));
         responseSOAP = sistarClientMDF2.solicitudProductoNominado(requestSoap);
         if (StringUtils.equals(responseSOAP.getCoderror(), "00")) {
            if (StringUtils.isBlank(responseSOAP.getExternalaccountnumber())) {
               response.addMensaje(getMensajeDetail(Mensajes.MD_SISTARBANC_ERROR.ordinal(), "Falta numero de cuenta en la respuesta"));
            } else {
               response.setRespuesta(modelMapper.map(responseSOAP, NewCardResponseDto.class));
               response.setResultado(true);
            }
         } else {
            response.addMensaje(getMensajeDetail(responseSOAP.getCoderror(), responseSOAP.getReason()));
         }
      } catch (Exception e) {
         logger.error("solicitudProductoNominado", e);
         if (Objects.nonNull(responseSOAP)) {
            response.addMensaje(getMensajeDetail(responseSOAP.getCoderror(), responseSOAP.getReason()));
         } else {
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
   }

   public InternalResponseDto<CreditResponseDto> recargaXCI(CreditRequestDto requestDto) {
      InternalResponseDto<CreditResponseDto> response;
      WSPagosAjustesxCIResponse responseSOAP = null;

      setComprobante(requestDto);
      boolean shouldGetAcctNum = shouldGetAcctNum(requestDto);
      response = getCreditResponseDtoInternalResponseDto(requestDto, responseSOAP);
      if(shouldGetAcctNum){
         try {
            if (response.getRespuesta().getCuenta() != null) {
               return response;
            }
         } catch (Exception ignore) { }
         IdentificationDto beneficiary = requestDto.getBeneficiario();
         String acct = getAccountNum(requestDto.getEmisor().longValue(),
             Long.parseLong(requestDto.getProducto()),
             beneficiary.getPais(),
             beneficiary.getDoc(),
             beneficiary.getTipoDoc()
         );
         if(acct != null){
            response.getRespuesta().setCuenta(acct);
         }else{
            response = new InternalResponseDto<>(new CreditResponseDto());
            response.setResultado(false);
            response.addMensaje(getMensajeDetail("99", "No se pudo obtener el número de cuenta para el documento/producto proporcionado"));
         }
      }
      return response;
   }
   private InternalResponseDto<CreditResponseDto> getCreditResponseDtoInternalResponseDto(CreditRequestDto requestDto, WSPagosAjustesxCIResponse responseSOAP) {
      InternalResponseDto<CreditResponseDto> response;
      final Integer forceBranchOffice = mappingUtils.getBranchOffice(requestDto.getCanal(), requestDto.getProducto());
      if(forceBranchOffice != null){
         requestDto.setSucursal(forceBranchOffice.toString());
      }

      try {
         WSPagosAjustesxCI requestSoap = modelMapper.map(requestDto, WSPagosAjustesxCI.class);
         responseSOAP = sistarTransactionClient.recargaXCI(requestSoap);
         response = processResponseSoapXCI(responseSOAP);
         if(response.getRespuesta() != null){
            response.getRespuesta().setItc("200.21");
            response.getRespuesta().setComprobante(requestDto.getComprobante());
            response.getRespuesta().setCuenta(responseSOAP.getWSPagosAjustesxCIResult().getRelacuentanume());
         }
      } catch (Exception e) {
         logger.error("recargaxCI", e);
         response = new InternalResponseDto<>();
         response.setResultado(false);
         if (Objects.nonNull(responseSOAP)) {
            response.addMensaje(getMensajeDetail(responseSOAP.getWSPagosAjustesxCIResult().getCodRetorno(),
                  responseSOAP.getWSPagosAjustesxCIResult().getMensajeRetorno()));
         } else {
            response.addMensaje(exceptionMessage(e));
         }
      }

      return response;
   }

   private String getAccountNum(Long issuer, Long product, String country, String document, String documentType){
      ProductsRequestDto request = new ProductsRequestDto();
      request.setDocument(document);
      request.setIssuer(issuer);
      request.setCountry(country);
      request.setDocumentType(documentType);
      request.setProduct(product);

      return Optional.of(getCuenta(request))
          .map(InternalResponseDto::getRespuesta)
          .map(AccountResponseDto::getRealAccount)
          .orElse(null);
   }

   private boolean shouldGetAcctNum(CreditRequestDto requestDto) {
      return config.getReturn_acct_on_recharge().contains(String.format("ch-%s", requestDto.getCanal()));
   }

   private void setComprobante(CreditRequestDto requestDto) {
      if(config.getHandle_receipt_on_channel().size() > 0){
         if(config.getHandle_receipt_on_channel().contains(requestDto.getCanal())){
            if(requestDto.getComprobante() == null && requestDto.getReferencia() != null){
               requestDto.setComprobante(requestDto.getReferencia());
            }
         }else{
            requestDto.setComprobante(null);
         }
      }
   }

   public InternalResponseDto<CreditResponseDto> recargaXCta(CreditRequestDto requestDto) {
      InternalResponseDto<CreditResponseDto> response;
      WSPagosAjustesResponse responseSOAP = null;

      setComprobante(requestDto);

      try {
         WSPagosAjustes requestSoap = modelMapper.map(requestDto, WSPagosAjustes.class);
         responseSOAP = sistarTransactionClient.recargaXCta(requestSoap);
         response = processResponseSoap(responseSOAP.getWSPagosAjustesResult(), CreditResponseDto.class);
      } catch (Exception e) {
         logger.error("recargaXCta", e);
         response = new InternalResponseDto<>();
         response.setResultado(false);
         if (Objects.nonNull(responseSOAP)) {
            response.addMensaje(getMensajeDetail(responseSOAP.getWSPagosAjustesResult().getCodRetorno(),
                  responseSOAP.getWSPagosAjustesResult().getMensajeRetorno()));
         } else {
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
   }

   public InternalResponseDto<TransferResponseDto> transferirC2CMultiproc(TransferRequestDto requestDto) {
      InternalResponseDto<TransferResponseDto> response;
      SistarbancMDTrfC2BAUTORIZACIONResponse responseSOAP = null;
      try {
         if(requestDto.getSucursal() == null){
            requestDto.setSucursal(config.getDefault_branch_office());
         }
         SistarbancMDTrfC2BAUTORIZACION requestSoap = modelMapper.map(requestDto, SistarbancMDTrfC2BAUTORIZACION.class);
         responseSOAP = sistarClientC2BMultiproc.autorizarTransferC2B(requestSoap);

         TransferResponseDto mappedResp = modelMapper.map(responseSOAP, TransferResponseDto.class);
         response = new InternalResponseDto<>(mappedResp);

         if(mappedResp.getSuccess()){
            TransferUpdateRequestDto confirmRequest = new TransferUpdateRequestDto();
            confirmRequest.setCuenta(requestDto.getOrdenante().getCuenta().toString());
            confirmRequest.setRrn(requestDto.getRrn());

            InternalResponseDto<TransferResponseDto> confirmResult = confirmTransferC2B(confirmRequest);
            if(!confirmResult.getRespuesta().getSuccess()){
               response = confirmResult;
            }
         }else{
            response = new InternalResponseDto<>(new TransferResponseDto(){{
               setSuccess(false);
            }});
            response.setResultado(false);
            response.addMensaje(getMensajeDetail(responseSOAP.getWsmidineroautorizaciontarjetaresult().getErrorCode(),
               responseSOAP.getWsmidineroautorizaciontarjetaresult().getErrorDescription()));
         }
      } catch (Exception e) {
         logger.error("ajuste", e);
         response = new InternalResponseDto<>();
         response.setResultado(false);
         if (Objects.nonNull(responseSOAP)) {
            response.addMensaje(getMensajeDetail(responseSOAP.getWsmidineroautorizaciontarjetaresult().getErrorCode(),
                  responseSOAP.getWsmidineroautorizaciontarjetaresult().getErrorDescription()));
         } else {
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
   }

   public InternalResponseDto<ProductsResponseDto> getProductos(ProductsRequestDto requestDto) {
      InternalResponseDto<ProductsResponseDto> response = new InternalResponseDto<>();
      ProductsResponseDto respDto = new ProductsResponseDto();
      respDto.getInfo().setProducts(new ArrayList<>());

      // Obtengo productos para cada uno de los id de emisor configurados
      Arrays.stream(sistarServiceProperties.getIssuers()).forEach(e -> {
         requestDto.setIssuer(Long.parseLong(e));
         List<ProductInfo> productsList = getProductInfo(requestDto, true);
         respDto.getInfo().getProducts().addAll(productsList);
      });

      //TODO obtener nombre/apellido del TH
      List<ProductInfo> prods = respDto.getInfo().getProducts();
      if(prods.size() > 0){
         if(prods.get(0).getTarjetas().size() > 0){
            respDto.getInfo().setName1(prods.get(0).getTarjetas().get(0).getName1());
            respDto.getInfo().setName2(prods.get(0).getTarjetas().get(0).getName2());
            respDto.getInfo().setLastName(prods.get(0).getTarjetas().get(0).getLastName1());
            respDto.getInfo().setLastName2(prods.get(0).getTarjetas().get(0).getLastName2());
         }
      }
      String fullName = Stream.of(
         respDto.getInfo().getName1(),
         respDto.getInfo().getName2(),
         respDto.getInfo().getLastName(),
         respDto.getInfo().getLastName2()
      )
      .filter(s -> s != null && !s.isEmpty())
      .collect(Collectors.joining(" "));

      respDto.getInfo().setName(fullName);
      respDto.setSuccess(true);
      response.setRespuesta(respDto);


      return response;
   }

   public InternalResponseDto<ReverseResponseDto> reverso(ReverseRequestDto requestDto) {
      String channel = StringUtils.defaultIfBlank(requestDto.getCanal(), "recargaGenerica");

      switch (channel) {
         case "recargaGenerica":
         case "terceros":
         case "creditos":
         case "sar":
            return reversoPorDocumento(requestDto);

         case "debitosAutomaticos":
         case "otrosProcesadores":
            if(requestDto.getReversaRecarga()){
               // Si se solicita reverso de una recarga, solicito reverso a travez de metodo de ajustes por documento en API Sistar
               return reversoPorCuenta(requestDto);
            }else{
               // Si se solicita reverso de un consumo, solicito reverso a travez de metodo de reverso de C2B
               TransferUpdateRequestDto reverseC2P = new TransferUpdateRequestDto();
               reverseC2P.setCuenta(requestDto.getCuenta());
               reverseC2P.setRrn(requestDto.getToReverseRrn());
               reverseC2P.setCanal(requestDto.getCanal());

               InternalResponseDto<TransferResponseDto> revResponse = reversarTransferenciaC2B(reverseC2P);

               ReverseResponseDto resp = new ReverseResponseDto();
               resp.setSuccess(revResponse.getRespuesta() != null && revResponse.getRespuesta().getSuccess());
               revResponse.setResultado(resp.getSuccess());

               InternalResponseDto<ReverseResponseDto> responseReverseC2P = new InternalResponseDto<>(resp);
               responseReverseC2P.setResultado(resp.getSuccess());
               return responseReverseC2P;
            }

         case "midinero": // C2C
            return reversarTransferenciaC2C(requestDto);

         case "transferenciaBanco": // C2b
            TransferUpdateRequestDto reverseC2B = new TransferUpdateRequestDto();
            reverseC2B.setCuenta(requestDto.getCuenta());
            reverseC2B.setCanal(requestDto.getCanal());
            reverseC2B.setRrn(requestDto.getToReverseRrn());

            InternalResponseDto<TransferResponseDto> reverseC2BResult = reversarTransferenciaC2B(reverseC2B);
            if(reverseC2BResult.getRespuesta() != null && reverseC2BResult.getRespuesta().getSuccess()){
               ReverseResponseDto convertedResponse = new ReverseResponseDto();
               convertedResponse.setItc(SistarConstants.ITC_C2B);
               convertedResponse.setSuccess(reverseC2BResult.getRespuesta().getSuccess());
               return new InternalResponseDto<>(convertedResponse);
            }else{
               InternalResponseDto<ReverseResponseDto> errorResp = new InternalResponseDto<>(new ReverseResponseDto(){{ setSuccess(false); }});
               errorResp.setResultado(false);
               reverseC2BResult.getMensajes()
                  .forEach(errorResp::addMensaje);
               return errorResp;
            }

         default:
            InternalResponseDto<ReverseResponseDto> response = new InternalResponseDto<>(new ReverseResponseDto());
            response.getRespuesta().setSuccess(false);
            response.addMensaje(mensajeService.obtenerMensajeDetail(
               new MensajeOrigen(Origenes.SISTAR, "Canal no soportado", "Canal no soportado")));
            return response;
      }
   }

   private InternalResponseDto<ReverseResponseDto> reversoPorDocumento(ReverseRequestDto requestDto) {
      InternalResponseDto<ReverseResponseDto> response;
      WSPagosAjustesxCIResponse responseSOAP = null;
      try {
         WSPagosAjustesxCI requestSoap = modelMapper.map(requestDto, WSPagosAjustesxCI.class);

         if (StringUtils.isEmpty(requestSoap.getRequestExternalIdARevertir())) {
            requestSoap.setRequestExternalId(ExternalIdUtils.getIdAsString(SistarConstants.MAX_EXTERNAL_ID_TRANSACTIONS));
         }
         responseSOAP = sistarTransactionClient.reversoPorDocumento(requestSoap);
         response = processResponseSoap(responseSOAP.getWSPagosAjustesxCIResult(), ReverseResponseDto.class);
      } catch (Exception e) {
         logger.error("reverso por documento", e);
         response = new InternalResponseDto<>();
         response.setResultado(false);
         if (responseSOAP != null) {
            response.addMensaje(mensajeService.obtenerMensajeDetail(
                  new MensajeOrigen(Origenes.SISTAR, responseSOAP.getWSPagosAjustesxCIResult().getCodRetorno(),
                        responseSOAP.getWSPagosAjustesxCIResult().getMensajeRetorno())));
         } else {
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
   }

   private InternalResponseDto<ReverseResponseDto> reversoPorCuenta(ReverseRequestDto requestDto) {
      InternalResponseDto<ReverseResponseDto> response;
      WSPagosAjustesResponse responseSOAP = null;
      try {
         WSPagosAjustes requestSoap = modelMapper.map(requestDto, WSPagosAjustes.class);

         responseSOAP = sistarTransactionClient.reversoPorCuenta(requestSoap);
         response = processResponseSoap(responseSOAP.getWSPagosAjustesResult(), ReverseResponseDto.class);
         response.setResultado(response.getRespuesta() != null ? response.getRespuesta().getSuccess(): false);
      } catch (Exception e) {
         logger.error("reverso x cuenta", e);
         response = new InternalResponseDto<>();
         response.setResultado(false);
         if (responseSOAP != null) {
            response.addMensaje(mensajeService.obtenerMensajeDetail(
                  new MensajeOrigen(Origenes.SISTAR, responseSOAP.getWSPagosAjustesResult().getCodRetorno(),
                        responseSOAP.getWSPagosAjustesResult().getMensajeRetorno())));
         } else {
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
   }

   public InternalResponseDto<BalanceResponseDto> balance(BalanceRequestDto requestDto) {
      InternalResponseDto<BalanceResponseDto> response;
      BalanceResponseDto respData = new BalanceResponseDto();
      respData.setSuccess(false);
      try {
         ConsultaDisponibleSistarbanc requestSoap = modelMapper.map(requestDto, ConsultaDisponibleSistarbanc.class);
         ConsultaDisponibleSistarbancResponse responseSOAP = sistarTransactionClient.balance(requestSoap);
         Optional<ConsultaDisponibleSistarbancResponse> resp = Optional.ofNullable(responseSOAP);

         Optional<Long> err = resp
            .map(ConsultaDisponibleSistarbancResponse::getConsultaDisponibleSistarbancResult)
            .map(AccountInfo::getErrorCodi).map(Long::parseLong);

         Optional<AccountCreditLineLocal> availableLocalCurrency = resp
            .map(ConsultaDisponibleSistarbancResponse::getConsultaDisponibleSistarbancResult)
            .map(AccountInfo::getAccountCreditLineLocal)
            .map(ArrayOfAccountCreditLineLocal::getAccountCreditLineLocal)
            .map(e -> e.get(0));

         Optional<AccountCreditLineExtranjera> availableForeignCurrency = resp
            .map(ConsultaDisponibleSistarbancResponse::getConsultaDisponibleSistarbancResult)
            .map(AccountInfo::getAccountCreditLineExtranjera)
            .map(ArrayOfAccountCreditLineExtranjera::getAccountCreditLineExtranjera)
            .map(e -> e.get(0));

         response = new InternalResponseDto<>(respData);
         if (err.isPresent() && err.get() == 0) {
            Info balanceInfoLocal = availableLocalCurrency.map(accountCreditLineLocal -> modelMapper.map(accountCreditLineLocal, Info.class)).orElse(null);
            Info balanceInfoForeign = availableForeignCurrency.map(accountCreditLineExtranjera -> modelMapper.map(accountCreditLineExtranjera, Info.class)).orElse(null);
            respData.setInfo(new ArrayList<Info>() {{
               if(balanceInfoLocal != null) {
                  if(config.getSet_negative_balance_to_cero()){
                     setBalanceToCero(balanceInfoLocal.getBalances());
                  }
                  add(balanceInfoLocal);
               }
               if(balanceInfoForeign != null){
                  if(config.getSet_negative_balance_to_cero()){
                     setBalanceToCero(balanceInfoForeign.getBalances());
                  }
                  add(balanceInfoForeign);
               }
            }});
            AccountVpsRequestDto accExceptionRequestDto = new AccountVpsRequestDto();
            AccountInfoRequestDto accCardRequestDto = new AccountInfoRequestDto();
            accCardRequestDto.setRealAccount(requestDto.getAccount());

            SistarbancMDF25STATUSCUENTATARJETAS requestCardSoap = modelMapper.map(accCardRequestDto, SistarbancMDF25STATUSCUENTATARJETAS.class);
            SistarbancMDF25STATUSCUENTATARJETASResponse responseCardSoap = sistarClientMDF25.obtenerInfoCuenta(requestCardSoap);
            if(responseCardSoap.getCoderror().equals("00")){
               AccountInfoResponseDto mappedResponse = modelMapper.map(responseCardSoap, AccountInfoResponseDto.class);

               accExceptionRequestDto.setRealAccount(Long.valueOf(requestDto.getAccount()));
               accExceptionRequestDto.setMarca(Long.valueOf(mappedResponse.getInfo().getProducto().getSello()));
               accExceptionRequestDto.setEmisor(Integer.valueOf(mappedResponse.getInfo().getProducto().getEmisor()));

               ExceptionFraudeConsul exceptionRequest = modelMapper.map(accExceptionRequestDto, ExceptionFraudeConsul.class);
               ExcepcionFraudeConsulResult exceptions =  sistarTransactionClient.estadoVps(exceptionRequest).getExceptionFraudeConsulResult();
               if(exceptions.getErrorCode() == 0 && exceptions.getListaExcepciones() != null){
                  for(EFConsul consul : exceptions.getListaExcepciones().getEFConsul()){
                     if(consul.getMoneCodi() == 1){
                        assert balanceInfoLocal != null;
                        balanceInfoLocal.setExcepciones(true);
                        balanceInfoLocal.setTefId(consul.getTefId());
                     }
                     else {
                        assert balanceInfoForeign != null;
                        balanceInfoForeign.setExcepciones(true);
                        balanceInfoForeign.setTefId(consul.getTefId());
                     }
                  }
               }
            }

            respData.setSuccess(true);
         } else {
            response = new InternalResponseDto<>(respData);
            response.setResultado(false);
            response.addMensaje(
                  mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.SISTAR, (err.map(Object::toString).orElse("")), "Unknown error getting data.")));
         }
      } catch (Exception e) {
         logger.error("balance", e);
         response = new InternalResponseDto<>(respData);
         response.addMensaje(exceptionMessage(e));
      }
      return response;
   }

   private <T> InternalResponseDto<T> processResponseSoapXCI(WSPagosAjustesxCIResponse responseSOAP) {
      InternalResponseDto<T> response;
      if (responseSOAP.getWSPagosAjustesxCIResult().getCodRetorno().isEmpty()
            || Integer.parseInt(responseSOAP.getWSPagosAjustesxCIResult().getCodRetorno()) == 0) {
         T responseDto = modelMapper.map(responseSOAP.getWSPagosAjustesxCIResult(), (Class<T>) CreditResponseDto.class);
         response = new InternalResponseDto<>(responseDto);
      } else {
         response = new InternalResponseDto<>(null);
         response.setResultado(false);
         response.addMensaje(mensajeService.obtenerMensajeDetail(
               new MensajeOrigen(Origenes.SISTAR, responseSOAP.getWSPagosAjustesxCIResult().getCodRetorno(),
                     responseSOAP.getWSPagosAjustesxCIResult().getMensajeRetorno())));
      }
      return response;
   }

   private <T> InternalResponseDto<T> processResponseSoap(PagosAjustesInfo info, Class<T> type) {
      InternalResponseDto<T> response;
      if (info.getCodRetorno().isEmpty() || NumberUtils.toInt(info.getCodRetorno(), 1) == 0) {
         T responseDto = modelMapper.map(info, type);
         response = new InternalResponseDto<>(responseDto);
       } else {
         response = new InternalResponseDto<>(null);
         response.setResultado(false);
         response.addMensaje(mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.SISTAR, info.getCodRetorno(), info.getMensajeRetorno())));
      }
      return response;
   }

   @SuppressWarnings("SimplifyStreamApiCallChains")
   public InternalResponseDto<InfoDto> getTransactions(TransactionsListRequestDto requestDto) {
      InternalResponseDto<InfoDto> response;
      SistarbancMDV2MOVENPERIODOResponse responseSOAP = null;

      // Restringe la cantidad de meses en el pasado de los que se puede consultar movimientos
      String variableLimit = "";
      if(config.getMax_months() != null){
         YearMonth month = YearMonth.now().plusMonths(config.getMax_months() * -1L);
         variableLimit = month.atDay(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
         // String end = month.atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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


      try {
         SistarbancMDV2MOVENPERIODO requestSoap = modelMapper.map(requestDto, SistarbancMDV2MOVENPERIODO.class);

         responseSOAP = sistarClientV2.movimientos(requestSoap);
         List<MOVENPERIODOITEM> responseSoapItems = responseSOAP.getMovenperiodo().getMOVENPERIODOITEM();

         boolean filtroManual = requestSoap.getDatefrom() != null && requestSoap.getDateto() != null && config.getForce_transactions_date_filter();
         if(filtroManual){
            DatatypeFactory df = DatatypeFactory.newInstance();
            requestSoap.getDateto().add(df.newDuration("P1D"));
//            requestSoap.getDatefrom().add(df.newDuration("P1D"));
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
      } catch (Exception e) {
         response = getErrorResponse(responseSOAP, e);
      }
      return response;
   }

   private InternalResponseDto<InfoDto> getErrorResponse(SistarbancMDV2MOVENPERIODOResponse responseSOAP, Exception e) {
      InternalResponseDto<InfoDto> response;
      logger.error("movimientos", e);
      response = new InternalResponseDto<>();
      response.setResultado(false);
      if (responseSOAP != null && responseSOAP.getMovenperiodo().getMOVENPERIODOITEM().size() > 0) {
         String reason = responseSOAP.getMovenperiodo().getMOVENPERIODOITEM().get(0).getREASON();
         response.addMensaje(mensajeService.obtenerMensajeDetail(
               new MensajeOrigen(Origenes.SISTAR, responseSOAP.getMovenperiodo().getMOVENPERIODOITEM().get(0).getERRORCODI(), reason)));
      } else {
         response.addMensaje(exceptionMessage(e));
      }
      return response;
   }

   public InternalResponseDto<NewCardResponseDto> solicitudProductoInnominado(NewCardRequestDto requestDto) {
      InternalResponseDto<NewCardResponseDto> response;
      CrearTarjetaPrepagaInnominadaResponse responseSOAP = null;

      if(StringUtils.isBlank(requestDto.getSucursal()) || requestDto.getSucursal().equals("0")){
         response = new InternalResponseDto<>();
         response.setResultado(false);
            response.addMensaje(getMensajeDetail("newcard.brach_office.invalid", "Debe indicar una sucursal válida."));
         return response;
      }

      if(StringUtils.isBlank(requestDto.getSucursalSolicitud()) || requestDto.getSucursalSolicitud().equals("0")){
         response = new InternalResponseDto<>();
         response.setResultado(false);
            response.addMensaje(getMensajeDetail("newcard.brach_office.invalid", "Debe indicar una sucursal válida."));
         return response;
      }

      try {
         CrearTarjetaPrepagaInnominada requestSoap = modelMapper.map(requestDto, CrearTarjetaPrepagaInnominada.class);
         requestSoap.getSolicitudPrepagaInnominada().setRequestExternalId(ExternalIdUtils.getIdAsString(MAX_EXTERNAL_ID_INNOMINADA));
         requestSoap.getSolicitudPrepagaInnominada().setRequestId(requestSoap.getSolicitudPrepagaInnominada().getRequestExternalId());
         responseSOAP = sistarTransactionClient.solicitudProductoInnominado(requestSoap);

         if (responseSOAP.getCrearTarjetaPrepagaInnominadaResult().getErrorCode() == 0) {
            NewCardResponseDto responseMap = modelMapper.map(responseSOAP.getCrearTarjetaPrepagaInnominadaResult(), NewCardResponseDto.class);
            response = new InternalResponseDto<>(responseMap);
         } else {
            response = new InternalResponseDto<>(null);
            response.setResultado(false);
            response.addMensaje(getMensajeDetail(responseSOAP.getCrearTarjetaPrepagaInnominadaResult().getErrorCode(),
                  responseSOAP.getCrearTarjetaPrepagaInnominadaResult().getErrorDescription()));
         }

      } catch (Exception e) {
         logger.error("solicitudProductoInnominado", e);
         response = new InternalResponseDto<>();
         response.setResultado(false);
         if (responseSOAP != null) {
            response.addMensaje(getMensajeDetail(responseSOAP.getCrearTarjetaPrepagaInnominadaResult().getErrorCode(),
                  responseSOAP.getCrearTarjetaPrepagaInnominadaResult().getErrorDescription()));
         } else {
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
   }

   public InternalResponseDto<CardsResponseDto> reemplazoProductoInnominado(ReemplazoRequestDto requestDto) {
      if(config.getReplace_card_by_account_enabled() && requestDto.getCuenta() != null && requestDto.getCuenta() != 0L){
         return reemplazoPorCuenta(requestDto);
      }else{
         return reemplazoPorDocumento(requestDto);
      }
   }

   public InternalResponseDto<CardsResponseDto> renovacionProductoInnominado(ReemplazoRequestDto requestDto) {
      return renovacionPorDocumento(requestDto);
   }

   private InternalResponseDto<CardsResponseDto> reemplazoPorDocumento(ReemplazoRequestDto requestDto) {
      InternalResponseDto<CardsResponseDto> response;
      ReemplazoTarjetaPrepagaInnominadaXCiResponse responseSOAP = null;
      try {
         if(requestDto.getEmisor() == null){
            throw new IllegalArgumentException("Sebe indicar codigo de emisor.");
         }
         ReemplazoTarjetaPrepagaInnominadaXCi requestSoap = modelMapper.map(requestDto, ReemplazoTarjetaPrepagaInnominadaXCi.class);

         responseSOAP = sistarTransactionClient.reemplazoProductoInnominadoXCI(requestSoap);
         if (responseSOAP.getReemplazoTarjetaPrepagaInnominadaXCiResult().getErrorCode() == 0) {
            CardsResponseDto r = new CardsResponseDto();
            r.setSuccess(true);
            r.setRealAccount(String.valueOf(requestDto.getCuenta()));
            response = new InternalResponseDto<>(r);
         } else {
            CardsResponseDto r = new CardsResponseDto();
            r.setSuccess(false);
            r.setErrors(responseSOAP.getReemplazoTarjetaPrepagaInnominadaXCiResult().getErrorDescription());
            response = new InternalResponseDto<>(r);
            response.setResultado(false);
            response.addMensaje(getMensajeDetail(responseSOAP.getReemplazoTarjetaPrepagaInnominadaXCiResult().getErrorCode(),
                  responseSOAP.getReemplazoTarjetaPrepagaInnominadaXCiResult().getErrorDescription()));
         }
      } catch (Exception e) {
         logger.error("reemplazoProductoInnominado", e);
         CardsResponseDto r = new CardsResponseDto();
         r.setSuccess(false);
         response = new InternalResponseDto<>(r);
         response.setResultado(false);
         if (responseSOAP != null) {
            response.addMensaje(getMensajeDetail(responseSOAP.getReemplazoTarjetaPrepagaInnominadaXCiResult().getErrorCode(),
                  responseSOAP.getReemplazoTarjetaPrepagaInnominadaXCiResult().getErrorDescription()));
         } else {
            response.addMensaje(getMensajeDetail("", e.getMessage()));
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
   }

   private InternalResponseDto<CardsResponseDto> renovacionPorDocumento(ReemplazoRequestDto requestDto) {
      InternalResponseDto<CardsResponseDto> response;
      RenovacionTarjetaPrepagaInnominadaXCiResponse responseSOAP = null;
      CardsResponseDto r = new CardsResponseDto(){{
         setSuccess(false);
      }};
      try {
         if(requestDto.getEmisor() == null){
            throw new IllegalArgumentException("Se debe indicar código de emisor.");
         }
         RenovacionTarjetaPrepagaInnominadaXCi requestSoap = modelMapper.map(requestDto, RenovacionTarjetaPrepagaInnominadaXCi.class);

         responseSOAP = sistarTransactionClient.renovacionProductoInnominadoXCI(requestSoap);
         if (responseSOAP.getRenovacionTarjetaPrepagaInnominadaXCiResult().getErrorCode() == 0) {
            r.setSuccess(true);
            r.setRealAccount(String.valueOf(requestDto.getCuenta()));
            response = new InternalResponseDto<>(r);
         } else {
            r.setErrors(responseSOAP.getRenovacionTarjetaPrepagaInnominadaXCiResult().getErrorDescription());
            response = new InternalResponseDto<>(r);
            response.setResultado(false);
            response.addMensaje(getMensajeDetail(responseSOAP.getRenovacionTarjetaPrepagaInnominadaXCiResult().getErrorCode(),
               responseSOAP.getRenovacionTarjetaPrepagaInnominadaXCiResult().getErrorDescription()));
         }
      } catch (Exception e) {
         logger.error("renovacionProductoInnominado", e);
         response = new InternalResponseDto<>(r);
         response.setResultado(false);
         if (responseSOAP != null) {
            response.addMensaje(getMensajeDetail(responseSOAP.getRenovacionTarjetaPrepagaInnominadaXCiResult().getErrorCode(),
               responseSOAP.getRenovacionTarjetaPrepagaInnominadaXCiResult().getErrorDescription()));
         } else {
            response.addMensaje(getMensajeDetail("", e.getMessage()));
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
   }

   private MensajeDetail exceptionMessage(Exception ex) {
      return getMensajeDetail("", ex.getMessage());
   }

   private InternalResponseDto<CardsResponseDto> reemplazoPorCuenta(ReemplazoRequestDto requestDto) {
      InternalResponseDto<CardsResponseDto> response;
      ReemplazoTarjetaPrepagaInnominadaResponse responseSOAP = null;
      try {
         ReemplazoTarjetaPrepagaInnominada requestSoap = modelMapper.map(requestDto, ReemplazoTarjetaPrepagaInnominada.class);

         responseSOAP = sistarTransactionClient.reemplazoProductoInnominadoXCuenta(requestSoap);
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
            response.addMensaje(getMensajeDetail(responseSOAP.getReemplazoTarjetaPrepagaInnominadaResult().getErrorCode(),
                  responseSOAP.getReemplazoTarjetaPrepagaInnominadaResult().getErrorDescription()));
         }
      } catch (Exception e) {
         logger.error("reemplazoProductoInnominado", e);
         CardsResponseDto r = new CardsResponseDto();
         r.setSuccess(false);
         response = new InternalResponseDto<>(r);
         response.setResultado(false);
         if (responseSOAP != null) {
            response.addMensaje(getMensajeDetail(responseSOAP.getReemplazoTarjetaPrepagaInnominadaResult().getErrorCode(),
                  responseSOAP.getReemplazoTarjetaPrepagaInnominadaResult().getErrorDescription()));
         } else {
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
   }

   private MensajeDetail getMensajeDetail(long errorCode, String errorDescription) {
      return getMensajeDetail(String.valueOf(errorCode), errorDescription);
   }

   private MensajeDetail getMensajeDetail(String errorCode, String errorDescription) {
      return mensajeService.obtenerMensajeDetail(new MensajeOrigen(Origenes.SISTAR, errorCode, errorDescription));
   }

   private List<ProductInfo> getProductInfo(ProductsRequestDto requestDto, boolean enableBlockingCodeFilter) {
      TarjetasxCI requestSoap = modelMapper.map(requestDto, TarjetasxCI.class);
      TarjetasxCIResponse responseSOAP = sistarClient3.getProductos(requestSoap);
      Set<String> prodCodes = new  HashSet<>();

      return responseSOAP
         .getTarjetas()
         .stream()
         // Ordeno por fecha de vencimiento descendente
         .sorted(
            Comparator.comparing(TarjetaCIBanco::getVencimiento_AAAAMM).reversed()
            .thenComparing(TarjetaCIBanco::getCodigo_de_Bloqueo)
         )
         // Normalmente si hay algun error o no se encuentran tarjetas, se devuelve un unico item
         // con un codigo y mensaje de error, en ese caso excluyo ese item, y el resultado es una colleccion vacía
         .filter(card -> Integer.parseInt(card.getCodigoError()) == 0)
         // Si esta habilitado el filtrado, filtro las tarjetas con estados de bloqueo configurados
         .filter(card -> !enableBlockingCodeFilter || config.getAvailable_cards_blocking_codes().contains(StringUtils.defaultIfBlank(card.getCodigo_de_Bloqueo(), "")))
         // Me quedo con la primer tarjeta o la que esté en estado normal
         .filter(card -> !prodCodes.contains(String.format("%s:%s", card.getCuenta(), card.getCodigo_Tipo_Producto())) || Integer.parseInt(card.getCodigo_de_Bloqueo()) == 0)
         // Voy llevando registro de cuales son los productos para los que se obtuvo tarjeta
         .peek(card -> prodCodes.add(String.format("%s:%s", card.getCuenta(), card.getCodigo_Tipo_Producto())))
         // Realizo el mapeo a ProductInfo
         .map(card -> modelMapper.map(card, ProductInfo.class))
         .collect(Collectors.toList());
   }

   public InternalResponseDto<AccountResponseDto> getCuenta(ProductsRequestDto requestDto) {
      InternalResponseDto<AccountResponseDto> response = new InternalResponseDto<>();
      final List<ProductInfo> productInfo = getProductInfo(requestDto, false);

      final List<ProductInfo> lstProductInfo = productInfo
            .stream()
            .filter(pi -> Objects.nonNull(pi.getProducto()))
            .filter(pi -> Objects.nonNull(pi.getProducto().getCodigo()))
            .filter(pi -> pi.getProducto().getCodigo().equals(requestDto.getProduct()))
            // .filter(pi -> ProductInfo.Card.Status.ACTIVE.equals(pi.getTarjetas().get(0).getEstado()))
            .limit(1)
            .collect(Collectors.toList());
      if (lstProductInfo.isEmpty()) {
         response.setResultado(false);
      } else {
         response.setRespuesta(new AccountResponseDto(lstProductInfo.get(0).getCuenta()));
      }
      return response;
   }

   public InternalResponseDto<TransferResponseDto> tranferirC2C(TransferRequestDto requestDto) {
      InternalResponseDto<TransferResponseDto> response;
      TransferenciaC2CResponse responseSOAP = null;
      try {
         TransferenciaC2C requestSoap = modelMapper.map(requestDto, TransferenciaC2C.class);
         String a = requestSoap.toString();
         responseSOAP = sistarTransactionClient.transferC2C(requestSoap);
         TransferResponseDto mappedResp = modelMapper.map(responseSOAP.getTransferenciaC2CResult(), TransferResponseDto.class);
         response = new InternalResponseDto<>(mappedResp);
         if(!response.getRespuesta().getSuccess()){
            response.addMensaje(getMensajeDetail(responseSOAP.getTransferenciaC2CResult().getErrorCode(), responseSOAP.getTransferenciaC2CResult().getErrorDescription()));
            response.setResultado(false);
         }
         return response;
      } catch (Exception e) {
         logger.error("tranferirC2C", e);
         response = new InternalResponseDto<>();
         response.setResultado(false);
         if (responseSOAP != null) {
            response.addMensaje(getMensajeDetail(responseSOAP.getTransferenciaC2CResult().getErrorCode(), responseSOAP.getTransferenciaC2CResult().getErrorDescription()));
         } else {
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
   }

   public InternalResponseDto<ReverseResponseDto> reversarTransferenciaC2C(ReverseRequestDto requestDto) {
      InternalResponseDto<ReverseResponseDto> response;
      Result responseSOAP = null;

      try {
         RevertirTransferenciaC2C requestSoap = modelMapper.map(requestDto, RevertirTransferenciaC2C.class);
         responseSOAP = sistarTransactionClient.reversarTransferC2C(requestSoap).getRevertirTransferenciaC2CResult();
         ReverseResponseDto mappedResp = modelMapper.map(responseSOAP, ReverseResponseDto.class);
         mappedResp.setReversalRrn(requestDto.getToReverseRrn());
         response = new InternalResponseDto<>(mappedResp);
         if(!response.getRespuesta().getSuccess()){
            response.addMensaje(getMensajeDetail(responseSOAP.getErrorCode(), responseSOAP.getErrorDescription()));
            response.setResultado(false);
         }
         return response;
      } catch (Exception e) {
         logger.error("reversarC2C", e);
         response = new InternalResponseDto<>();
         response.setResultado(false);
         if (responseSOAP != null) {
            response.addMensaje(getMensajeDetail(responseSOAP.getErrorCode(), responseSOAP.getErrorDescription()));
         } else {
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
   }

   public InternalResponseDto<TransferResponseDto> tranferirC2B(TransferRequestDto requestDto) {
      InternalResponseDto<TransferResponseDto> response;
      SistarbancMDTrfC2BAUTORIZACIONResponse responseSOAP = null;
      try {
         SistarbancMDTrfC2BAUTORIZACION requestSoap = modelMapper.map(requestDto, SistarbancMDTrfC2BAUTORIZACION.class);
         responseSOAP = sistarClientC2BMultiproc.autorizarTransferC2B(requestSoap);
         TransferResponseDto mappedResp = modelMapper.map(responseSOAP, TransferResponseDto.class);
         response = new InternalResponseDto<>(mappedResp);
         if(!response.getRespuesta().getSuccess()){
            response.addMensaje(getMensajeDetail(responseSOAP.getWsmidineroautorizaciontarjetaresult().getErrorCode(),
               responseSOAP.getWsmidineroautorizaciontarjetaresult().getErrorDescription()));
            response.setResultado(false);
         }
         return response;
      } catch (Exception e) {
         logger.error("tranferirC2B", e);
         response = new InternalResponseDto<>();
         response.setResultado(false);
         if (responseSOAP != null) {
            response.addMensaje(getMensajeDetail(responseSOAP.getWsmidineroautorizaciontarjetaresult().getErrorCode(),
               responseSOAP.getWsmidineroautorizaciontarjetaresult().getErrorDescription()));
         } else {
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
   }

   public InternalResponseDto<TransferResponseDto> confirmTransferC2B(TransferUpdateRequestDto requestDto) {
      InternalResponseDto<TransferResponseDto> response;
      SistarbancMDTrfC2BCONFIRMACIONResponse responseSOAP = null;

      try {
         SistarbancMDTrfC2BCONFIRMACION requestSoap = modelMapper.map(requestDto, SistarbancMDTrfC2BCONFIRMACION.class);
         responseSOAP = sistarClientC2BMultiproc.confirmarTransferC2B(requestSoap);
         TransferResponseDto mappedResp = modelMapper.map(responseSOAP, TransferResponseDto.class);
         response = new InternalResponseDto<>(mappedResp);
         if(!response.getRespuesta().getSuccess()){
            WSMiDineroConfirmarAutorizacionPrepagaResult result = responseSOAP.getWsmidineroconfirmarautorizacionprepagaresult();
            response.addMensaje(getMensajeDetail(result.getErrorCode(), result.getErrorDescription()));
            response.setResultado(false);
         }
         return response;
      } catch (Exception e) {
         logger.error("confirmarC2B", e);
         response = new InternalResponseDto<>();
         response.setResultado(false);
         if (responseSOAP != null) {
            WSMiDineroConfirmarAutorizacionPrepagaResult result = responseSOAP.getWsmidineroconfirmarautorizacionprepagaresult();
            response.addMensaje(getMensajeDetail(result.getErrorCode(), result.getErrorDescription()));
         } else {
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
   }

   public InternalResponseDto<TransferResponseDto> reversarTransferenciaC2B(TransferUpdateRequestDto requestDto) {
      InternalResponseDto<TransferResponseDto> response;
      SistarbancMDTrfC2BREVERSOResponse responseSOAP = null;

      try {
         SistarbancMDTrfC2BREVERSO requestSoap = modelMapper.map(requestDto, SistarbancMDTrfC2BREVERSO.class);
         responseSOAP = sistarClientC2BMultiproc.reversarTransferC2B(requestSoap);
         TransferResponseDto mappedResp = modelMapper.map(responseSOAP, TransferResponseDto.class);
         response = new InternalResponseDto<>(mappedResp);
         if(!response.getRespuesta().getSuccess()){
            WSMiDineroResult result = responseSOAP.getWsmidineroresult();
            response.addMensaje(getMensajeDetail(result.getErrorCode(), result.getErrorDescription()));
            response.setResultado(false);
         }
         return response;
      } catch (Exception e) {
         logger.error("reversarC2B", e);
         response = new InternalResponseDto<>();
         response.setResultado(false);
         if (responseSOAP != null) {
            WSMiDineroResult result = responseSOAP.getWsmidineroresult();
            response.addMensaje(getMensajeDetail(result.getErrorCode(), result.getErrorDescription()));
         } else {
            response.addMensaje(exceptionMessage(e));
         }
      }
      return response;
  }

   public InternalResponseDto<CardsResponseDto> actualizarEstadoTarjeta(CardStatusUpdateDto requestDto) {
      InternalResponseDto<CardsResponseDto> response;
      CardsResponseDto mappedResp;
      String errReason, errCode;

      try {
         if(sistarServiceProperties.isUse_block_card_alt_method()){
            WSCambioEstadoCuentaExecuteResponse responseSOAP;
            WSCambioEstadoCuentaExecute requestSoap = modelMapper.map(requestDto, WSCambioEstadoCuentaExecute.class);
            responseSOAP = sistarClientUpdCardStatus.cambiarEstadoTarjeta(requestSoap);
            mappedResp = modelMapper.map(responseSOAP, CardsResponseDto.class);
            errCode = responseSOAP.getOutcambioestado().getCoderror();
            errReason = responseSOAP.getOutcambioestado().getReason();
         }else{
            SistarbancMDF2CAMBIOESTADOTARJETAResponse responseSOAP;
            SistarbancMDF2CAMBIOESTADOTARJETA requestSoap = modelMapper.map(requestDto, SistarbancMDF2CAMBIOESTADOTARJETA.class);
            responseSOAP = sistarClientMDF2.cambiarEstadoTarjeta(requestSoap);
            mappedResp = modelMapper.map(responseSOAP, CardsResponseDto.class);
            errReason = responseSOAP.getReason();
            errCode = responseSOAP.getCoderror();
         }

         response = new InternalResponseDto<>(mappedResp);
         response.setResultado(mappedResp.getSuccess());
         if(!response.isResultado()){
            response.addMensaje(getMensajeDetail(errCode, errReason));
         }

         return response;
      } catch (Exception e) {
         logger.error("actualizarEstado", e);
         response = new InternalResponseDto<>(new CardsResponseDto());
         response.getRespuesta().setSuccess(false);
         response.setMensajes(new ArrayList<>());
         response.setResultado(false);
         response.addMensaje(getMensajeDetail("99", e.getCause().getMessage()));
         // response.addMensaje(mensajeService.obtenerMensajeDetail(Mensajes.MD_SISTARBANC_ERROR));
      }
      return response;
   }

   public InternalResponseDto<PagoServiciosResponse> solicitarPago(PagoServiciosRequestDto requestDto) {
      InternalResponseDto<PagoServiciosResponse> response = new InternalResponseDto<>();
      PagoServicioCajaPrepagaResponse responseSoap = null;

      try {
         PagoServicioCajaPrepaga requestSoap =  modelMapper.map(requestDto, PagoServicioCajaPrepaga.class);
         responseSoap = sistarTransactionClient.solicitarPago(requestSoap);
         PagoServiciosResponse responseData = modelMapper.map(responseSoap, PagoServiciosResponse.class);
         if(responseData.getCodigoResultado() == 0){
            response.setRespuesta(responseData);
         }else{
            response.setResultado(false);
            response.addMensaje(getMensajeDetail(responseData.getCodigoResultado(), responseSoap.getPagoServicioCajaPrepagaResult().getErrorDescription()));
         }
      }
      catch (Exception e) {
         logger.error("solicitarPago", e);
         response = new InternalResponseDto<>();
         response.setResultado(false);
         if (responseSoap != null) {
            response.addMensaje(getMensajeDetail(responseSoap.getPagoServicioCajaPrepagaResult().getErrorCode(),
                    responseSoap.getPagoServicioCajaPrepagaResult().getErrorDescription()));
         } else {
            //TODO
            // response.addMensaje(mensajeService.obtenerMensajeDetail(Mensajes.MD_SISTARBANC_ERROR));
            response.addMensaje(getMensajeDetail("99", e.getMessage()));

         }
      }
      return response;
   }

   public InternalResponseDto<AccountInfoResponseDto> obtenerInfoCuenta(AccountInfoRequestDto requestDto) {
      InternalResponseDto<AccountInfoResponseDto> response = null;
      SistarbancMDF25STATUSCUENTATARJETAS requestSoap;

      try{
         requestSoap = modelMapper.map(requestDto, SistarbancMDF25STATUSCUENTATARJETAS.class);
         SistarbancMDF25STATUSCUENTATARJETASResponse responseSoap = sistarClientMDF25.obtenerInfoCuenta(requestSoap);
         if(responseSoap.getCoderror().equals("00")){
            AccountInfoResponseDto mappedResponse = modelMapper.map(responseSoap, AccountInfoResponseDto.class);
            response = new InternalResponseDto<>(mappedResponse);
         }else{
            response = new InternalResponseDto<>(new AccountInfoResponseDto());
            response.getRespuesta().setSuccess(false);
            response.setResultado(false);
            response.addMensaje(getMensajeDetail(responseSoap.getCoderror(), responseSoap.getReason()));
         }
      }catch(Exception ex){
         assert response != null;
         response.addMensaje(new MensajeDetail(){{
            setCodigo("");
            setMensaje(ex.getMessage());
         }});
         response.setResultado(false);
      }
      return response;
   }

   public InternalResponseDto<AccountVPSResponseDto> cambiarEstadoVps(AccountVpsRequestDto requestDto) {
      InternalResponseDto<AccountVPSResponseDto> response;
      ExceptionFraudeModiResponse responseSOAP;
      InternalResponseDto<AccountVPSResponseDto> result;
      try {
         result = consultaVps(requestDto);

         List<ExceptionFraude> currentExceptions = result.getRespuesta().getInfo().getExceptionFraude();

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
               if(requestDto.getDisable()){
                  AccountVPSResponseDto respDto = new AccountVPSResponseDto();
                  respDto.setSuccess(true);
                  respDto.getInfo().getExceptionFraude().addAll(currentExceptions);

                  return new InternalResponseDto<>(respDto);
               }
            }else{ // No existe actualmente una excepción de control de límites para la moneda dada

               // Si se solicita habilitar el control de límites, devuelvo éxito, ya que no existe una excepción activa.
               if(requestDto.getEnable()){
                  AccountVPSResponseDto respDto = new AccountVPSResponseDto();
                  respDto.setSuccess(true);
                  respDto.getInfo().getExceptionFraude().addAll(currentExceptions);

                  return new InternalResponseDto<>(respDto);
               }else{
                  // Se está solicitando deshabilitar el control de limites, o sea, dar de alta una nueva excepción

                  // Si no se indica fecha de inicio y fin, los seteo de manera que el rango sea de una hora, desde la hora actual
                  if(StringUtils.isBlank(requestDto.getInicio())){
                     LocalDateTime now = LocalDateTime.now();
                     ZoneId zoneId = ZoneId.of(mappingUtils.getConfig().getLocal_timezone());
                     requestDto.setInicio(ZonedDateTime.of(now, zoneId).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                     requestDto.setFin(ZonedDateTime.of(now.plusHours(1), zoneId).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                  }

                  // Se solicita el alta de una excepcion contra la API SB
                  InternalResponseDto<AccountVPSResponseDto> altaResult = altaVps(requestDto);
                  if(altaResult.getRespuesta().getSuccess()){
                     altaResult.getRespuesta().getInfo().getExceptionFraude().addAll(currentExceptions);
                     altaResult.getRespuesta().getInfo().getExceptionFraude().add(new ExceptionFraude(){{
                        setId(altaResult.getRespuesta().getInfo().getId());
                        setFrom(requestDto.getInicio().replaceAll("T", " "));
                        setTo(requestDto.getFin().replaceAll("T", " "));
                        setCurrency(requestDto.getMoneda().longValue());
                     }});
                  }
                  return altaResult;
               }
            }
         }

         // Solicito a API SB cambio de estado de una excepción.
         ExceptionFraudeModi requestSoap = modelMapper.map(requestDto, ExceptionFraudeModi.class);
         responseSOAP =  sistarTransactionClient.cambioEstadoVps(requestSoap);
         if(responseSOAP.getExceptionFraudeModiResult().getErrorCode() == 0){
            AccountVPSResponseDto mappedResponse = modelMapper.map(responseSOAP.getExceptionFraudeModiResult(), AccountVPSResponseDto.class);
            response = new InternalResponseDto<>(mappedResponse);
            response.getRespuesta().getInfo().getExceptionFraude().addAll(
                currentExceptions.stream()
                    .filter(e -> !Objects.equals(e.getId(), requestDto.getId()))
                    .collect(Collectors.toList())
            );
         }else{
            response = new InternalResponseDto<>(new AccountVPSResponseDto(){{ setSuccess(false); }});
            response.addMensaje(getMensajeDetail(responseSOAP.getExceptionFraudeModiResult().getErrorCode(),
                    responseSOAP.getExceptionFraudeModiResult().getErrorDescription()));
            response.getRespuesta().setSuccess(false);
            response.setResultado(false);
         }
      }catch(Exception ex){
         response = new InternalResponseDto<>(new AccountVPSResponseDto());
         response.setResultado(false);
         response.addMensaje(new MensajeDetail(){{
            setCodigo("");
            setMensaje(ex.getMessage());
         }});
      }
      return response;
   }

   public InternalResponseDto<AccountVPSResponseDto> altaVps(AccountVpsRequestDto requestDto) {
      InternalResponseDto<AccountVPSResponseDto> response = null;
      ExceptionFraudeAltaResponse responseSOAP;

      try{
         ExceptionFraudeAlta requestSoap = modelMapper.map(requestDto, ExceptionFraudeAlta.class);
         Integer offset = config.getLimit_control_start_offset_minutes();
          requestSoap.getExceptionFraudeAlta().getFechaDesde().add(
              DatatypeFactory.newInstance().newDuration(String.format("%sPT%sM", offset < 0 ? "-": "", Math.abs(offset)))
          );
         if(requestSoap.getExceptionFraudeAlta().getMoneCodi() == 0){
            requestSoap.getExceptionFraudeAlta().setMoneCodi(requestDto.getMoneda());
         }
         responseSOAP =  sistarTransactionClient.altaVps(requestSoap);
         if(responseSOAP.getExceptionFraudeAltaResult().getErrorCode() == 0){
            AccountVPSResponseDto mappedResponse = modelMapper.map(responseSOAP.getExceptionFraudeAltaResult(), AccountVPSResponseDto.class);
            response = new InternalResponseDto<>(mappedResponse);
         }else{
            response = new InternalResponseDto<>(new AccountVPSResponseDto(){{ setSuccess(false); }});
            response.addMensaje(getMensajeDetail(responseSOAP.getExceptionFraudeAltaResult().getErrorCode(),
                    responseSOAP.getExceptionFraudeAltaResult().getErrorDescription()));
            response.getRespuesta().setSuccess(false);
            response.setResultado(false);
         }
      }catch(Exception ex){
         assert response != null;
         response.addMensaje(new MensajeDetail(){{
            setCodigo("");
            setMensaje(ex.getMessage());
         }});
      }
      return response;
   }

   public InternalResponseDto<AccountVPSResponseDto> consultaVps(AccountVpsRequestDto requestDto) {
      InternalResponseDto<AccountVPSResponseDto> response;
      ExceptionFraudeConsulResponse responseSOAP;

      try{
         ExceptionFraudeConsul requestSoap = modelMapper.map(requestDto, ExceptionFraudeConsul.class);
         responseSOAP =  sistarTransactionClient.estadoVps(requestSoap);
         AccountVPSResponseDto mappedResponse = modelMapper.map(responseSOAP.getExceptionFraudeConsulResult(), AccountVPSResponseDto.class);
         response = new InternalResponseDto<>(mappedResponse);
         response.setResultado(mappedResponse.getSuccess());
         if(requestDto.getMoneda() != null){
            response.getRespuesta().getInfo().getExceptionFraude().removeIf(e -> e.getCurrency().intValue() != requestDto.getMoneda());
         }
      }catch(Exception ex){
         response = new InternalResponseDto<>();
         response.addMensaje(new MensajeDetail(){{
            setCodigo("");
            setMensaje(ex.getMessage());
         }});
      }
      return response;
   }

   private void setBalanceToCero(List<Balance> balances){
      for(Balance currentBalance : balances){
         if (currentBalance.getBalance() < 0)
            currentBalance.setBalance((double) 0);
      }
   }

   public String fitoEcho() {
      SistarbancMDF25ECHOTESTResponse response = sistarClientMDF25.fitoEchoTest();
      return response.getResult();
   }

  public String getCotizacionDelDia() {
   TasaDelDiaDolaresResponse response = sistarTransactionClient.getCotizacionDelDia();
   return response.getTasaDelDiaDolaresResult().getAny().toString();
  }

    public InternalResponseDto<TransactionInfoResponseDto> getTransaction(TransactionInfoRequestDto requestDto) {
       InternalResponseDto<TransactionInfoResponseDto> response = null;
       ConsultaPorReferencia requestSoap;

       try{
          requestSoap = modelMapper.map(requestDto, ConsultaPorReferencia.class);
          ConsultaPorReferenciaResponse responseSoap = sistarTransactionClient.obtenerInfoTransaccion(requestSoap);
          if(responseSoap.getConsultaPorReferenciaResult().getErrorCode() == 0){
             TransactionInfoResponseDto mappedResponse = modelMapper.map(responseSoap, TransactionInfoResponseDto.class);
             response = new InternalResponseDto<>(mappedResponse);
          }else{
             response = new InternalResponseDto<>(new TransactionInfoResponseDto());
             response.getRespuesta().setSuccess(false);
             response.setResultado(false);
             ConsultaPorReferenciaResult result = responseSoap.getConsultaPorReferenciaResult();
             String[] errorMsgLines = result.getErrorDescription().split("\n");
             response.addMensaje(getMensajeDetail(result.getErrorCode(), errorMsgLines.length > 0 ? errorMsgLines[0]: ""));
             response.setResultado(response.getRespuesta().isSuccess());
          }
       }catch(Exception ex){
          assert response != null;
          response.addMensaje(new MensajeDetail(){{
             setCodigo("");
             setMensaje(ex.getMessage());
          }});
          response.setResultado(false);
       }
       return response;
    }
}
