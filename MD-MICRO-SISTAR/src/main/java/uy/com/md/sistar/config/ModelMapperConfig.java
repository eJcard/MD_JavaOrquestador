package uy.com.md.sistar.config;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider.ProvisionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uy.com.md.common.in.PagoServiciosRequestDto;
import uy.com.md.common.movimientos.TransaccionesDto;
import uy.com.md.common.out.PagoServiciosResponse;
import uy.com.md.sistar.dto.CodeNameTuple;
import uy.com.md.sistar.dto.IdentificationDto;
import uy.com.md.sistar.dto.TarjetasxCIResponse.TarjetaCIBanco;
import uy.com.md.sistar.dto.in.*;
import uy.com.md.sistar.dto.out.*;
import uy.com.md.sistar.dto.out.AccountInfoDto.ClientInfo;
import uy.com.md.sistar.dto.out.BalanceResponseDto.Info;
import uy.com.md.sistar.dto.out.ProductInfo.Card;
import uy.com.md.sistar.dto.out.ProductInfo.Product;
import uy.com.md.sistar.dto.out.TransactionInfo.AdditionalData;
import uy.com.md.sistar.dto.out.TransactionInfo.AdditionalData.BankAccountInfo;
import uy.com.md.sistar.dto.out.TransactionInfo.AdditionalData.CustomerInfo;
import uy.com.md.sistar.service.SistarServiceProperties;
import uy.com.md.sistar.soap.client.*;
import uy.com.md.sistar.soap.client2.*;
import uy.com.md.sistar.util.ExternalData;
import uy.com.md.sistar.util.SistarMappingUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import static uy.com.md.sistar.util.SistarConstants.*;

@Configuration
public class ModelMapperConfig {

   private ModelMapper modelMapper = null;

   @Autowired
   SistarServiceProperties config;

   @Autowired
   private ExternalData externalData;

   @Autowired
   SistarMappingUtils sistarMappingUtils;

   @Value("${sistar.sistar_api.username}")
   private String fitoAPIUsername;

   @Value("${sistar.sistar_api.user}")
   private String sistarApiUserCode;

   @Value("${sistar.sistar_api.password}")
   private String fitoAPIPassword;

   @Value("${sistar.wsmidinero_api.username}")
   private String infinitusAPIUsername;

   @Value("${sistar.wsmidinero_api.password}")
   private String infinitusAPIPassword;

   @Value("${sistar.api_upd_status.username}")
   private String apiUpdStatusUsername;

   @Value("${sistar.api_upd_status.password}")
   private String apiUpdStatusPassword;

   @Bean
   public ModelMapper modelMapper() {
      if (Objects.isNull(modelMapper)) {
         modelMapper = initModelMapper();
      }
      return modelMapper;
   }

   private ModelMapper initModelMapper() {
      ModelMapper mapper = new ModelMapper();
      mapBeneficiary(mapper);
      mapAddress(mapper);
      mapNewCardRequest(mapper);
      mapNewCardResponse(mapper);
      mapCreditXCIRequest(mapper);
      mapCreditXCtaRequest(mapper);
      mapCreditResponse(mapper);
      mapCreditReverseRequest(mapper);
      mapCreditReverseResponse(mapper);
      mapGetProductsRequest(mapper);
      mapGetProductsResponse(mapper);
      mapGetBalanceRequest(mapper);
      mapGetBalanceResponse(mapper);
      mapGetTransactionsRequest(mapper);
      mapGetTransactionsResponse(mapper);
      mapNewCardUnnamedRequest(mapper);
      mapPersonalAddress(mapper);
      mapCardUnnamedReplacementRequest(mapper);
      mapCardUnnamedRenew(mapper);
      mapC2BRequests(mapper);
      mapC2BReponse(mapper);
      mapCardStatusUpdate(mapper);
      mapAccountInfoRequest(mapper);
      mapC2CRequests(mapper);
      mapC2CResponse(mapper);
      mapC2CReverseRequest(mapper);
      mapC2CReverseResponse(mapper);
      mapPaymentWithPINRequest(mapper);
      mapPaymentWithPINResponse(mapper);
      mapC2CMultiprocRequest(mapper);
      mapC2CMultiprocResponse(mapper);
      mapTransactionInfo(mapper);
      mapAccountVpsRequest(mapper);
      mapVpsToExceptionFraude2Request(mapper);
      mapExceptionFraudeAltaResponse(mapper);
      mapAccountVpsRequestToConsul(mapper);
      mapVpsToExceptionFraudeConsul2Request(mapper);
      mapExceptionFraudeConsulResponse(mapper);
      mapEFConsul(mapper);
      mapAccountVpsModiRequest(mapper);
      mapVpsToExceptionFraudeModificacionRequest(mapper);
      mapExceptionFraudeModiResponse(mapper);
      return mapper;
   }

    private void mapTransactionInfo(ModelMapper mapper) {
       mapper.createTypeMap(TransactionInfoRequestDto.class, ConsultaPorReferencia.class)
           .addMapping(src -> infinitusAPIPassword, ConsultaPorReferencia::setPassws)
           .addMapping(src -> infinitusAPIUsername, ConsultaPorReferencia::setUsuariows)
           .addMapping(TransactionInfoRequestDto::getEmisor, ConsultaPorReferencia::setInEmisor)
           .addMapping(TransactionInfoRequestDto::getRrnRequest, ConsultaPorReferencia::setRequestExternalId)
           .addMapping(TransactionInfoRequestDto::getRrnTransaction, ConsultaPorReferencia::setInRequestExternalIdAConsultar);

      mapper.createTypeMap(ConsultaPorRef.class, TransactionInfoResponseDto.class)
          .addMapping(ConsultaPorRef::getCodAutorizacion, TransactionInfoResponseDto::setAuthorizationCode)
          .addMapping(ConsultaPorRef::getIdReferenciaAConsultar, TransactionInfoResponseDto::setId)
          .addMapping(ConsultaPorRef::getCuentaTarjeta, TransactionInfoResponseDto::setAccount)
          .addMapping(ConsultaPorRef::getDescripcion, TransactionInfoResponseDto::setDesc)
          .addMappings(m -> m.using(toIsoCurrencyConverter).map(ConsultaPorRef::getMoneda, TransactionInfoResponseDto::setCurrency))
          .addMappings(m -> m.using(stringToBigDecimalConverter).map(ConsultaPorRef::getImporte, TransactionInfoResponseDto::setAmount))
          .addMapping(ConsultaPorRef::getSucursal, TransactionInfoResponseDto::setBranchOffice)
          .addMappings(m -> m.using(addSeparatorToDateConverter).map(ConsultaPorRef::getFechaAplicacion, TransactionInfoResponseDto::setSettlementDate))
          .addMappings(m -> m.using(addSeparatorToDateConverter).map(ConsultaPorRef::getFechaValor, TransactionInfoResponseDto::setValueDate))
          .addMappings(m -> m.using(addSeparatorToTimeConverter).map(ConsultaPorRef::getHoraAplicacion, TransactionInfoResponseDto::setSettlementTime))
          .addMapping(ConsultaPorRef::getCodMovimiento, TransactionInfoResponseDto::setSbCode)
          .addMapping(ConsultaPorRef::getSubCodMovimiento, TransactionInfoResponseDto::setSbSubCode)
          .addMapping(ConsultaPorRef::getTipoWs, TransactionInfoResponseDto::setType)
          .addMappings(m -> m.using(resultCodeStringToSuccessFlagConverter).map(ConsultaPorRef::getCodRetorno, TransactionInfoResponseDto::setSuccess));

      mapper.createTypeMap(ConsultaPorReferenciaResult.class, TransactionInfoResponseDto.class)
           .setProvider(p -> {
             if(p.getSource() == null){
               return null;
             }
             ConsultaPorReferenciaResult source = (ConsultaPorReferenciaResult) p.getSource();
             ConsultaPorRef data = source.getListaConsul().getConsultaPorRef().stream().findFirst().orElse(null);
             return data != null ? mapper.map(data, TransactionInfoResponseDto.class): null;
           });

       mapper.createTypeMap(ConsultaPorReferenciaResponse.class, TransactionInfoResponseDto.class)
           .setProvider(p -> {
               if(p.getSource() == null){
                   return null;
               }
               ConsultaPorReferenciaResponse source = (ConsultaPorReferenciaResponse) p.getSource();
               return mapper.map(source.getConsultaPorReferenciaResult(), TransactionInfoResponseDto.class);
           });
    }

    private void mapPaymentWithPINRequest(ModelMapper mapper) {

      mapper.createTypeMap(PagoServiciosRequestDto.class, PagoServicioCajaPrepaga2.class)
         .addMapping(src -> infinitusAPIUsername, PagoServicioCajaPrepaga2::setUsuario)
         .addMapping(src -> infinitusAPIUsername, PagoServicioCajaPrepaga2::setUsuaCodi)
         .addMapping(src -> infinitusAPIPassword, PagoServicioCajaPrepaga2::setPassword)
         .addMapping(PagoServiciosRequestDto::getRrn, PagoServicioCajaPrepaga2::setRequestId)
         .addMapping(PagoServiciosRequestDto::getRrn, PagoServicioCajaPrepaga2::setRequestExternalId)
         .addMapping(PagoServiciosRequestDto::getMarca, PagoServicioCajaPrepaga2::setMarcaCodi)
         .addMapping(PagoServiciosRequestDto::getEmisor, PagoServicioCajaPrepaga2::setPagaOtorEntiCodi)
         .addMapping(src -> 4, PagoServicioCajaPrepaga2::setIdenMetodo)
         .addMappings(m -> m.using(stringToIntConverter).map(src -> src.getOrdenante().getTipoDoc(), PagoServicioCajaPrepaga2::setIdenNumeTipo))
         .addMapping(src -> src.getOrdenante().getDoc(), PagoServicioCajaPrepaga2::setIdenNume)
         .addMappings(m -> m.using(stringToIntConverter).map(PagoServiciosRequestDto::getProducto, PagoServicioCajaPrepaga2::setIdenProducCodi))
         .addMappings(m -> m.using(tipoMovConverter).map(src -> src, PagoServicioCajaPrepaga2::setTipoMov))
         .addMappings(m -> m.using(currencyISOtoIdConverter).<Short>map(PagoServiciosRequestDto::getMoneda, (dest, value) -> dest.setMoneCodi(Short.toUnsignedInt(value))))
         .addMappings(m -> m.using(bigDecimalToDoubleConverter).map(PagoServiciosRequestDto::getMonto, PagoServicioCajaPrepaga2::setImporte))
         .addMappings(m -> m.using(bigDecimalToDoubleConverter).map(PagoServiciosRequestDto::getMontoIva, PagoServicioCajaPrepaga2::setImporteIva))
         .addMapping(PagoServiciosRequestDto::getPinblk, PagoServicioCajaPrepaga2::setPin)
          .addMapping(PagoServiciosRequestDto::getRef, PagoServicioCajaPrepaga2::setNumeroDeComprobante);

      mapper.createTypeMap(PagoServiciosRequestDto.class, PagoServicioCajaPrepaga.class)
         .addMapping(src -> src, PagoServicioCajaPrepaga::setParameters);
   }

   private void mapPaymentWithPINResponse(ModelMapper mapper) {
      mapper.createTypeMap(PagoServicioCajaPrepagaResult.class, PagoServiciosResponse.class)
         .addMapping(PagoServicioCajaPrepagaResult::getErrorCode, PagoServiciosResponse::setCodigoResultado)
         .addMappings(m -> m.using(resultCodeLongToSuccessFlagConverter).map(PagoServicioCajaPrepagaResult::getErrorCode, PagoServiciosResponse::setSuccess))
         .addMapping(PagoServicioCajaPrepagaResult::getRechaMotiCodi, PagoServiciosResponse::setRechaMotiCodi)
         .addMapping(PagoServicioCajaPrepagaResult::getRechaMotiDescri, PagoServiciosResponse::setRechaMotiDescri)
         .addMappings(m -> m.using(longToIntegerConverter).map(PagoServicioCajaPrepagaResult::getAutoId, PagoServiciosResponse::setCodigoAutorizacion))
         .addMapping(src -> "200.00", PagoServiciosResponse::setItc);

      mapper.createTypeMap(PagoServicioCajaPrepagaResponse.class, PagoServiciosResponse.class)
         .setProvider(provider -> {
            if(provider.getSource() == null){
               return null;
            }

            PagoServicioCajaPrepagaResponse source = (PagoServicioCajaPrepagaResponse) provider.getSource();
           return mapper.map(source.getPagoServicioCajaPrepagaResult(), PagoServiciosResponse.class);
         });
   }

   private void mapAccountInfoRequest(ModelMapper mapper) {
      mapper.createTypeMap(AccountInfoRequestDto.class, SistarbancMDF25STATUSCUENTATARJETAS.class)
         .addMapping(src -> fitoAPIUsername, SistarbancMDF25STATUSCUENTATARJETAS::setUsuariows)
         .addMapping(src -> fitoAPIPassword, SistarbancMDF25STATUSCUENTATARJETAS::setPassws)
         .addMapping(AccountInfoRequestDto::getRealAccount, SistarbancMDF25STATUSCUENTATARJETAS::setExternalaccountnumber);

      mapper.createTypeMap(TarjetDTO.class, TelefonoDto.class);
      
      mapper.createTypeMap(TarjetDTO.class, Product.class)
         .addMappings(m -> m.using(stringToLongConverter).map(TarjetDTO::getTipoProducto, Product::setCodigo))
         .addMappings(m -> m.using(productDescritionConverter).map(TarjetDTO::getTipoProductoDescri, Product::setNombre))
         .addMapping(TarjetDTO::getTipoProducto, Product::setCodigo)
         .addMapping(TarjetDTO::getMarca, Product::setSello)
         .addMapping(TarjetDTO::getEmisor, Product::setEmisor)
         .addMapping(TarjetDTO::getSucursalEmisora, Product::setSucursalEmisora)
         .addMappings(m -> m.using(gafConverter2).map(src -> src, Product::setGrupoAfinidad))
      ;
      
      mapper.createTypeMap(TarjetDTO.class, Card.class)
         .addMappings(m -> m.using(lastFourDigitsConverter).map(TarjetDTO::getCardnumber, Card::setUltimosCuatro))
         .addMappings(m -> m.using(lastDayOfMonthConverter).map(TarjetDTO::getDuedate, Card::setExpiracion))
         .addMappings(m -> m.using(byteToIntConverter).map(TarjetDTO::getCardstatuscode, Card::setCodigoBloqueo))
         .addMappings(m -> m.using(cardStatusConverter2).map(TarjetDTO::getEstado, Card::setEstado))
      ;
      
      mapper.createTypeMap(TarjetDTO.class, uy.com.md.sistar.dto.DireccionDto.class);
      
      mapper.createTypeMap(TarjetDTO.class, DocDto.class)
         .addMapping(TarjetDTO::getDocumentNumber, DocDto::setNum)
         .addMapping(TarjetDTO::getDocumentCountry, DocDto::setPais)
         .addMappings(m -> m.using(docTypeNameConverter).map(TarjetDTO::getDocumentType, DocDto::setTipoDoc));
      
      mapper.createTypeMap(TarjetDTO.class, ClientInfo.class)
         .setProvider(p -> {
            if(p.getSource() == null){
               return null;
            }
            ClientInfo ret = new ClientInfo();
            TarjetDTO card = (TarjetDTO) p.getSource();
            ret.setDoc(mapper.map(card, DocDto.class));
            return ret;
         })
         .addMapping(TarjetDTO::getName1, ClientInfo::setNombre)
         .addMapping(TarjetDTO::getLastName1, ClientInfo::setApellido)
         .addMappings(m -> m.using(stringToDateConverter).map(TarjetDTO::getBirthdate, ClientInfo::setFechaDeNacimiento))
         .addMapping(TarjetDTO::getNationality, ClientInfo::setNacionalidad)
         .addMapping(TarjetDTO::getGender, ClientInfo::setSexo)
         .addMapping(TarjetDTO::getOperaPorTerceros, (dest, value) -> dest.setOperaPorTerceros(BooleanUtils.toBoolean((String)value)));

     mapper.createTypeMap(TarjetDTO.class, AccountInfoDto.class)
      .setProvider(provider -> {
         if(provider.getSource() == null){
            return null;
         }
         TarjetDTO data = (TarjetDTO) provider.getSource();
         AccountInfoDto accountInfo = new AccountInfoDto();
         accountInfo.setCliente(mapper.map(data, ClientInfo.class));
         accountInfo.setProducto(mapper.map(data, Product.class));
         accountInfo.setTarjeta(mapper.map(data, Card.class));
         return accountInfo;
      });

      mapper.createTypeMap(SistarbancMDF25STATUSCUENTATARJETASResponse.class, AccountInfoDto.class)
         .setProvider(provider -> {
            if(provider == null){
               return null;
            }
            SistarbancMDF25STATUSCUENTATARJETASResponse data = (SistarbancMDF25STATUSCUENTATARJETASResponse) provider.getSource();
            Optional<TarjetDTO> card = data.getCards().getTarjetDTO()
                  .stream().min(Comparator
                        .comparingInt(TarjetDTO::getCardstatuscode)
                        .thenComparing(Comparator.comparing(TarjetDTO::getDuedate).reversed()));
            AccountInfoDto info = card.isPresent() ? modelMapper.map(card.get(), AccountInfoDto.class) : new AccountInfoDto();
            info.getProducto().getGrupoAfinidad().setCodigo(Integer.toUnsignedLong(data.getAfinitygroupcode()));

            return info;
         });

      mapper.createTypeMap(SistarbancMDF25STATUSCUENTATARJETASResponse.class, AccountInfoResponseDto.class)
      .addMappings(m -> m.using(resultCodeStringToSuccessFlagConverter).map(SistarbancMDF25STATUSCUENTATARJETASResponse::getCoderror, AccountInfoResponseDto::setSuccess))
      .addMapping(src -> src, AccountInfoResponseDto::setInfo);

   }

   private void mapCardStatusUpdate(ModelMapper mapper) {
      mapper.createTypeMap(CardStatusUpdateDto.class, SistarbancMDF2CAMBIOESTADOTARJETA.class)
        .addMapping(src -> fitoAPIUsername, SistarbancMDF2CAMBIOESTADOTARJETA::setUsuariows)
        .addMapping(src -> fitoAPIPassword, SistarbancMDF2CAMBIOESTADOTARJETA::setPassws)
        .addMapping(CardStatusUpdateDto::getAccount, SistarbancMDF2CAMBIOESTADOTARJETA::setExternalaccountnumber)
        .addMapping(src -> null, SistarbancMDF2CAMBIOESTADOTARJETA::setStatustype)
        .addMappings(
           m -> m.using(statusToBlockingCodeConverter).map(CardStatusUpdateDto::getStatus, SistarbancMDF2CAMBIOESTADOTARJETA::setStatuscode)
        );

      mapper.createTypeMap(SistarbancMDF2CAMBIOESTADOTARJETAResponse.class, CardsResponseDto.class)
        .addMappings(m -> m.using(resultCodeStringToSuccessFlagConverter)
           .map(SistarbancMDF2CAMBIOESTADOTARJETAResponse::getCoderror, CardsResponseDto::setSuccess)
        )
        .addMappings(m -> m.using(statusChangeErrorConverter).map(src -> src, CardsResponseDto::setErrors));

     mapper.createTypeMap(CardStatusUpdateDto.class, InCambioEstadoCuenta.class)
       .addMapping(src -> apiUpdStatusUsername, InCambioEstadoCuenta::setUsuariows)
       .addMappings(m -> m.using(md5Converter).map(src -> apiUpdStatusPassword, InCambioEstadoCuenta::setPassws))
       .addMapping(CardStatusUpdateDto::getAccount, InCambioEstadoCuenta::setCuenta)
       .addMappings(m -> m.using(currentStateConverter).map(CardStatusUpdateDto::getStatus, InCambioEstadoCuenta::setEstadoActual))
       .addMappings(m -> m.using(newStateConverter).map(CardStatusUpdateDto::getStatus, InCambioEstadoCuenta::setNuevoEstado));

     mapper.createTypeMap(CardStatusUpdateDto.class, WSCambioEstadoCuentaExecute.class)
      .addMapping(src -> src, WSCambioEstadoCuentaExecute::setIncambioestado);

     mapper.createTypeMap(OutCambioEstado.class, CardsResponseDto.class)
       .addMappings(m -> m.using(resultCodeStringToSuccessFlagConverter).map(OutCambioEstado::getCoderror, CardsResponseDto::setSuccess))
       .addMapping(OutCambioEstado::getReason, CardsResponseDto::setErrors)
     ;

     mapper.createTypeMap(WSCambioEstadoCuentaExecuteResponse.class, CardsResponseDto.class)
       .setProvider(p -> {
         if(p.getSource() == null){
           return null;
         }
         WSCambioEstadoCuentaExecuteResponse resp = (WSCambioEstadoCuentaExecuteResponse) p.getSource();
         return mapper.map(resp.getOutcambioestado(), CardsResponseDto.class);
       });
   }

   private final Converter<String, String> requestExternalIdConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      String id = ctx.getSource();
      if(id.matches("^[CR].*$")) {
         return id.substring(1);
      } else {
         return id;
      }
   };

   private final Converter<Byte, Integer> byteToIntConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      return Byte.toUnsignedInt(ctx.getSource());
   };

   private final Converter<String,Date> stringToDateConverter = ctx -> {
      if(ctx.getSource() != null){
         String strDate = ctx.getSource();
         DateTimeFormatter f = new DateTimeFormatterBuilder()
               .appendPattern("[dd-MM-yyyy]"
                  + "[dd/MM/yyyy]"
                  + "[yyyy-MM-dd]"
                  + "[yyyy/MM/dd]"
                  + "[yyMM]")
               .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
               .toFormatter();
         LocalDate lastDayOfMonth = LocalDate.parse(strDate, f);
         return java.sql.Date.valueOf(lastDayOfMonth);
      }
      return null;
   };

   // Devuelve hash md5 de el String indicado
   private final Converter<String, String> md5Converter = ctx -> {
     if(ctx.getSource() == null){
       return null;
     }
     String password = ctx.getSource();
     return DigestUtils.md5Hex(password);
   };
  private final Converter<String, String> currentStateConverter = ctx -> {
    if(ctx.getSource() == null){
      return null;
    }
    String normalizedState = ctx.getSource().toUpperCase();
    Map<Card.Status, String> opositeStates = new HashMap<Card.Status, String>()
    {{
      put(Card.Status.ACTIVE, "D");
      put(Card.Status.SUSPENDED, "A");
      put(Card.Status.SUSPENDED_ON_REQUEST, "A");
    }};
    return opositeStates.getOrDefault(Card.Status.valueOf(normalizedState), null);
  };

  private final Converter<String, String> newStateConverter = ctx -> {
    if(ctx.getSource() == null){
      return null;
    }
    if(ctx.getSource() == null){
      return null;
    }
    String normalizedState = ctx.getSource().toUpperCase();
    Map<Card.Status, String> opositeStates = new HashMap<Card.Status, String>()
    {{
      put(Card.Status.ACTIVE, "A");
      put(Card.Status.SUSPENDED, "S");
      put(Card.Status.SUSPENDED_ON_REQUEST, "D");
    }};
    return opositeStates.getOrDefault(Card.Status.valueOf(normalizedState), null);
  };

  private final Converter<PagoServiciosRequestDto, String> tipoMovConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      PagoServiciosRequestDto source = ctx.getSource();
      return source.getIsVoid() ? "1": "0";
   };

   private final Converter<SistarbancMDF2CAMBIOESTADOTARJETAResponse, String> statusChangeErrorConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      return ctx.getSource().getReason();
   };

   private final Converter<Long, String> longToStringConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      return String.valueOf(ctx.getSource());
   };

   private final Converter<BigDecimal, Double> bigDecimalToDoubleConverter = ctx -> ctx.getSource() != null ? ctx.getSource().doubleValue(): null;
   
   private final Converter<String, String> productDescritionConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      String description = ctx.getSource();
      return description.contains("/") ? description.substring(0, description.indexOf("/")).trim(): description;
   };

   private final Converter<Long, Integer> longToIntegerConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      return ctx.getSource().intValue();
   };

   private void mapC2BReponse(ModelMapper mapper) {
      mapper.createTypeMap(WSMiDineroAutorizacionTarjetaResult.class, TransferResponseDto.class)
         .addMappings(m -> m.using(longToStringConverter).map(WSMiDineroAutorizacionTarjetaResult::getErrorCode, TransferResponseDto::setCodigoResultado))
         .addMappings(m -> m.using(resultCodeLongToSuccessFlagConverter).map(WSMiDineroAutorizacionTarjetaResult::getErrorCode, TransferResponseDto::setSuccess))
         .addMappings(m -> m.using(longToStringConverter).map(WSMiDineroAutorizacionTarjetaResult::getErrorCode, TransferResponseDto::setItc))
         .addMappings(m -> m.using(longToStringConverter).map(WSMiDineroAutorizacionTarjetaResult::getCodConfirmacion, TransferResponseDto::setCodigoAutorizacion));
      
      mapper.createTypeMap(SistarbancMDTrfC2BAUTORIZACIONResponse.class, TransferResponseDto.class)
         .setProvider(provider -> {
            if(provider.getSource() == null){
               return null;
            }
            SistarbancMDTrfC2BAUTORIZACIONResponse response = (SistarbancMDTrfC2BAUTORIZACIONResponse) provider.getSource();
           return mapper.map(response.getWsmidineroautorizaciontarjetaresult(), TransferResponseDto.class);
         });
      
      mapper.createTypeMap(WSMiDineroConfirmarAutorizacionPrepagaResult.class, TransferResponseDto.class)
         .addMappings(m -> m.using(longToStringConverter).map(WSMiDineroConfirmarAutorizacionPrepagaResult::getErrorCode, TransferResponseDto::setCodigoResultado))
         .addMappings(m -> m.using(resultCodeLongToSuccessFlagConverter).map(WSMiDineroConfirmarAutorizacionPrepagaResult::getErrorCode, TransferResponseDto::setSuccess))
         .addMappings(m -> m.using(toStringConverter).map(WSMiDineroConfirmarAutorizacionPrepagaResult::getConsuCupon, TransferResponseDto::setCodigoAutorizacion));
      
      mapper.createTypeMap(SistarbancMDTrfC2BCONFIRMACIONResponse.class, TransferResponseDto.class)
         .setProvider(provider -> {
            if(provider.getSource() == null){
               return null;
            }
            SistarbancMDTrfC2BCONFIRMACIONResponse response = (SistarbancMDTrfC2BCONFIRMACIONResponse) provider.getSource();
           return mapper.map(response.getWsmidineroconfirmarautorizacionprepagaresult(), TransferResponseDto.class);
         });
   
      mapper.createTypeMap(WSMiDineroResult.class, TransferResponseDto.class)
         .addMappings(m -> m.using(longToStringConverter).map(WSMiDineroResult::getErrorCode, TransferResponseDto::setCodigoResultado))
         .addMappings(m -> m.using(resultCodeLongToSuccessFlagConverter).map(WSMiDineroResult::getErrorCode, TransferResponseDto::setSuccess));
      
      mapper.createTypeMap(SistarbancMDTrfC2BREVERSOResponse.class, TransferResponseDto.class)
         .setProvider(provider -> {
            if(provider.getSource() == null){
               return null;
            }
            SistarbancMDTrfC2BREVERSOResponse response = (SistarbancMDTrfC2BREVERSOResponse) provider.getSource();
           return mapper.map(response.getWsmidineroresult(), TransferResponseDto.class);
         });
}

   private void mapC2BRequests(ModelMapper mapper) {
      // TODO
      mapper.createTypeMap(TransferRequestDto.class, MDAutorizacion.class)
         .setProvider(provider -> {
            if(provider.getSource() == null){
               return null;
            }
            TransferRequestDto source = (TransferRequestDto) provider.getSource();
            MDAutorizacion ret = new MDAutorizacion();

            boolean debit = source.getCanal().equalsIgnoreCase("otrosprocesadores");
            String channel = source.getCanal().toLowerCase() + (debit ? "-debit": "");

            Integer code = source.getCanal() != null ? sistarMappingUtils.getCodeByChannel(channel): null;
            String subcode = source.getCanal() != null ? sistarMappingUtils.getSubCodeByChannel(channel): null;
            
           ret.setCODMOVIMIENTO(code != null ? code.longValue() : 0);
           ret.setSUBCODMOVIMIENTO(Integer.parseInt(Objects.requireNonNull(subcode)));

            return ret;
         })
         .addMapping(src -> src.getOrdenante().getCuenta(), MDAutorizacion::setNROCUENTA)
         .addMapping(TransferRequestDto::getMoneda, MDAutorizacion::setMONEDA)
         .addMappings(m -> m.using(bigDecimalToDoubleConverter).map(TransferRequestDto::getMonto, MDAutorizacion::setIMPORTE))
         .addMappings(m -> m.using(stringToLongConverter).map(TransferRequestDto::getRrn, MDAutorizacion::setIDTRANSACCION));

      mapper.createTypeMap(TransferRequestDto.class, MDConfirmacion.class)
         .addMapping(src -> src.getOrdenante().getCuenta(), MDConfirmacion::setNROCUENTA)
         .addMappings(m -> m.using(stringToLongConverter).map(TransferRequestDto::getRrn, MDConfirmacion::setIDTRANSACCION));

      mapper.createTypeMap(TransferRequestDto.class, MDReverso.class)
         .addMapping(src -> src.getOrdenante().getCuenta(), MDReverso::setNROCUENTA)
         .addMappings(m -> m.using(stringToLongConverter).map(TransferRequestDto::getRrn, MDReverso::setIDTRANSACCION));

      mapper.createTypeMap(TransferUpdateRequestDto.class, MDConfirmacion.class)
         .addMapping(TransferUpdateRequestDto::getCuenta, MDConfirmacion::setNROCUENTA)
         .addMappings(m -> m.using(stringToLongConverter).map(TransferUpdateRequestDto::getRrn, MDConfirmacion::setIDTRANSACCION));

      mapper.createTypeMap(TransferUpdateRequestDto.class, MDReverso.class)
         .addMapping(TransferUpdateRequestDto::getCuenta, MDReverso::setNROCUENTA)
         .addMappings(m -> m.using(stringToLongConverter).map(TransferUpdateRequestDto::getRrn, MDReverso::setIDTRANSACCION));

      mapper.createTypeMap(TransferRequestDto.class, SistarbancMDTrfC2BAUTORIZACION.class)
         .addMapping(src -> fitoAPIUsername, SistarbancMDTrfC2BAUTORIZACION::setUsuariows)
         .addMapping(src -> fitoAPIPassword, SistarbancMDTrfC2BAUTORIZACION::setPassws)
         .addMapping(src -> sistarApiUserCode, SistarbancMDTrfC2BAUTORIZACION::setUsuario)
         .addMapping(src -> src, SistarbancMDTrfC2BAUTORIZACION::setMdautorizacion);

      mapper.createTypeMap(TransferUpdateRequestDto.class, SistarbancMDTrfC2BCONFIRMACION.class)
         .addMapping(src -> fitoAPIUsername, SistarbancMDTrfC2BCONFIRMACION::setUsuariows)
         .addMapping(src -> fitoAPIPassword, SistarbancMDTrfC2BCONFIRMACION::setPassws)
         .addMapping(src -> sistarApiUserCode, SistarbancMDTrfC2BCONFIRMACION::setUsuario)
         .addMapping(src -> src, SistarbancMDTrfC2BCONFIRMACION::setMdconfirmacion);
      
      mapper.createTypeMap(TransferUpdateRequestDto.class, SistarbancMDTrfC2BREVERSO.class)
         .addMapping(src -> fitoAPIUsername, SistarbancMDTrfC2BREVERSO::setUsuariows)
         .addMapping(src -> fitoAPIPassword, SistarbancMDTrfC2BREVERSO::setPassws)
         .addMapping(src -> sistarApiUserCode, SistarbancMDTrfC2BREVERSO::setUsuario)
         .addMapping(src -> src, SistarbancMDTrfC2BREVERSO::setMdreverso);
   }

   private void mapC2CRequests(ModelMapper mapper) {
      mapper.createTypeMap(TransferRequestDto.class, TransferenciaC2C.class)
              .addMappings(m -> m.using(c2c2TransferConverter).map(src -> src, TransferenciaC2C::setTransferenciaC2C));
   }

   private void mapC2CReverseRequest(ModelMapper mapper){
       mapper.createTypeMap(ReverseRequestDto.class, RevertirTransferenciaC2C.class)
               .addMappings(m -> m.using(c2cRevertTransferConverter).map(src -> src, RevertirTransferenciaC2C::setRevertirTransferenciaC2C));
   }

   private void mapC2CReverseResponse(ModelMapper mapper){
      mapper.createTypeMap(Result.class, ReverseResponseDto.class)
         .addMappings(m -> m.using(resultCodeLongToSuccessFlagConverter).map(Result::getErrorCode,
                  ReverseResponseDto::setSuccess))
         .addMappings(m -> m.using(resultCodeLongToItc)
                  .map(Result::getErrorCode, ReverseResponseDto::setItc)
         );
   }

   private void mapC2CResponse(ModelMapper mapper) {
      mapper.createTypeMap(TransferenciaC2CResult.class, TransferResponseDto.class)
         .addMapping(TransferenciaC2CResult::getErrorCode, TransferResponseDto::setCodigoResultado)
         .addMapping(TransferenciaC2CResult::getCobraCupon, TransferResponseDto::setCobraCupon)
         .addMappings(m -> m.using(toStringConverter).map(TransferenciaC2CResult::getConsuCupon, TransferResponseDto::setCodigoAutorizacion))
         .addMapping(TransferenciaC2CResult::getConsuCupon, TransferResponseDto::setConsuCupon)
         .addMappings(m -> m.using(resultCodeLongToSuccessFlagConverter)
            .map(Result::getErrorCode, TransferResponseDto::setSuccess)
         )
         .addMappings(m -> m.using(resultCodeLongToItc)
            .map(Result::getErrorCode, TransferResponseDto::setItc)
         );
   }

   private void mapCreditReverseResponse(ModelMapper mapper) {
      mapper.createTypeMap(PagosAjustesInfo.class, ReverseResponseDto.class)
            .addMapping(PagosAjustesInfo::getCodAutorizacion, ReverseResponseDto::setReversalRrn)
            .addMappings(m -> m.using(resultCodeStringToSuccessFlagConverter).map(PagosAjustesInfo::getCodRetorno,
                  ReverseResponseDto::setSuccess));
   }

   private void mapCreditResponse(ModelMapper mapper) {
      mapper.createTypeMap(PagosAjustesInfo.class, CreditResponseDto.class)
            .addMapping(PagosAjustesInfo::getCodAutorizacion, CreditResponseDto::setCodigoAutorizacion)
            .addMappings(m -> m.using(resultCodeStringToSuccessFlagConverter).map(PagosAjustesInfo::getCodRetorno,
                  CreditResponseDto::setSuccess));
   }

   private void mapC2CMultiprocResponse(ModelMapper mapper) {
      mapper.createTypeMap(PagosAjustesInfo.class, TransferResponseDto.class)
            .addMapping(PagosAjustesInfo::getCodAutorizacion, TransferResponseDto::setCodigoAutorizacion)
            .addMappings(
               m -> m.using(resultCodeStringToSuccessFlagConverter).map(PagosAjustesInfo::getCodRetorno, TransferResponseDto::setSuccess)
            );
   }

   private void mapGetTransactionsResponse(ModelMapper mapper) {
      mapper.createTypeMap(MOVENPERIODOITEM.class, TransaccionesDto.class)
         .setProvider(req -> new TransaccionesDto())
         .addMappings(m -> m.using(movementTypeToITCConverter).map(src -> src, TransaccionesDto::setCodigo))
         .addMappings(m -> m.using(dateToStringDateTimeConverter).map(MOVENPERIODOITEM::getTRANSACTIONDATE, TransaccionesDto::setFechaHora))
         .addMappings(m -> m.using(dateToStringDateConverter).map(MOVENPERIODOITEM::getCONFIRMATIONDATE, TransaccionesDto::setFechaAplicacion))
         .addMappings(m -> m.using(dateToStringDateConverter).map(MOVENPERIODOITEM::getTRANSACTIONDATE, TransaccionesDto::setFechaPresentacion))
         .addMappings(m -> m.using(longToStringConverter).map(MOVENPERIODOITEM::getIDMOVEMENT, TransaccionesDto::setSbInfinitusCode))
         .addMappings(m -> m.using(movementFieldsToRrnConverter).map(src -> src, TransaccionesDto::setRrn))
         .addMappings(m -> m.using(requestExternalIdConverter).map(MOVENPERIODOITEM::getREQUESTEXTERNALID, TransaccionesDto::setRequestExternalId))
         .addMappings(m -> m.using(toIsoCurrencyConverter).map(src -> String.valueOf(src.getCARDHOLDERCURRENCY()), TransaccionesDto::setMoneda))
         .addMappings(m -> m.using(toIsoCurrencyConverter).map(src -> String.valueOf(src.getORIGINALCURRENCY()), TransaccionesDto::setMonedaOrigen))
         .addMapping(src -> String.valueOf(src.getORIGINALAMOUNT()), TransaccionesDto::setMontoOrigen)
         .addMappings(m -> m.using(cleanAuthorizationCodeConverter).map(MOVENPERIODOITEM::getAUTHORIZATIONCODE, TransaccionesDto::setCodigoAprobacion))
         .addMapping(src -> String.valueOf(src.getMCC()), TransaccionesDto::setMcc)
         .addMapping(MOVENPERIODOITEM::getMERCHANTNAME, TransaccionesDto::setComercioDescripcion)
         .addMapping(MOVENPERIODOITEM::getCODTX, TransaccionesDto::setSbCode)
         .addMapping(MOVENPERIODOITEM::getSUBCODTX, TransaccionesDto::setSbSubcode)
         .addMapping(MOVENPERIODOITEM::getESTADOTIPO, TransaccionesDto::setSbStatusType)
         .addMapping(MOVENPERIODOITEM::getESTADOCODI, TransaccionesDto::setSbStatusCode)
         .addMapping(MOVENPERIODOITEM::getOBSERVACIONES, TransaccionesDto::setObservaciones)
         .addMappings(m -> m.using(statusToVoidCountConverter).map(src -> src, TransaccionesDto::setVoidCount))
         .addMappings(m -> m.using(statusToReversalCountConverter).map(src -> src, TransaccionesDto::setReversalCount))
         .addMappings(m -> m.using(amountReversalConverter).map(src -> src, TransaccionesDto::setMonto));

      mapper.createTypeMap(MOVENPERIODOITEM.class, TransactionInfo.class)
         .setProvider(req -> {
            TransactionInfo info = new TransactionInfo();
            info.setAdditionalData(new AdditionalData());
            info.getAdditionalData().setRequester(new CustomerInfo());
            info.getAdditionalData().setBank(new BankAccountInfo());
            return info;
         })
         .<String>addMapping(MOVENPERIODOITEM::getACCOUNTNUMBER, (dest, value) -> dest.getAdditionalData().getRequester().setAccount(value))
         .addMapping(src -> BigDecimal.valueOf(src.getSETTLEMENTAMOUNT()), TransactionInfo::setAmount)
         .addMapping(src -> String.valueOf(src.getSETTLEMENTCURRENCY()), TransactionInfo::setCurrency)
         .addMapping(src -> BigDecimal.valueOf(src.getORIGINALAMOUNT()), TransactionInfo::setOriginalAmount)
         .addMapping(src -> String.valueOf(src.getORIGINALCURRENCY()), TransactionInfo::setOriginalCurrency)
         .<String>addMapping(MOVENPERIODOITEM::getACCOUNTNUMBER, (dest, value) -> dest.getAdditionalData().getRequester().setAccount(value))
         .addMappings(m -> m.using(trimStringConverter).map(MOVENPERIODOITEM::getAUTHORIZATIONCODE, TransactionInfo::setApprovalCode))
         .addMapping(MOVENPERIODOITEM::getMCC, TransactionInfo::setMcc)
         .addMappings(
            m -> m.using(multipleToSingleSpaceConverter).map(MOVENPERIODOITEM::getMERCHANTNAME, TransactionInfo::setMerchantDescription)
         )
         .addMappings(
            m -> m.using(transactionCodeConverter).map(src -> src, TransactionInfo::setCode)
         )
         .addMappings(
            m -> m.using(transactionStateConverter).map(src -> src, TransactionInfo::setStatus)
         )
         .addMappings(
            m -> m.using(transactionDescConverter).map(src -> src, TransactionInfo::setDescription)
         )
         .addMappings(
            m -> m.using(calendarToDateConverter).map(MOVENPERIODOITEM::getTRANSACTIONDATE, TransactionInfo::setDate)
         )
         .addMappings(
            m -> m.using(isConfirmedConverter).map(src -> src, TransactionInfo::setConfirmed)
         );
   }

   private void mapGetBalanceResponse(ModelMapper mapper) {
      mapper.createTypeMap(AccountCreditLineLocal.class, Balance.class)
            .addMapping(src -> "858", Balance::setMoneda)
            .addMapping(src -> "Pesos", Balance::setNombre)
            .addMapping(AccountCreditLineLocal::getAvailable, Balance::setBalance);

      mapper.createTypeMap(AccountCreditLineExtranjera.class, Balance.class)
            .addMapping(src -> "840", Balance::setMoneda)
            .addMapping(src -> "Dólares", Balance::setNombre)
            .addMapping(AccountCreditLineExtranjera::getAvailable, Balance::setBalance);
      
      mapper.createTypeMap(AccountCreditLineLocal.class, BalanceResponseDto.Info.class)
         .setProvider(provider -> createBalanceInfoInstance(mapper, provider));

      mapper.createTypeMap(AccountCreditLineExtranjera.class, BalanceResponseDto.Info.class)
         .setProvider(provider -> createBalanceInfoInstance(mapper, provider));
   }

   private Info createBalanceInfoInstance(ModelMapper mapper, ProvisionRequest<Info> provider) {
      if(provider == null || !(provider.getSource() instanceof AccountCreditLine)){
         return null;
      }
      AccountCreditLine data = (AccountCreditLine) provider.getSource();

      BalanceResponseDto.Info info = new BalanceResponseDto.Info();
      Balance balance = mapper.map(data, Balance.class);
      info.getBalances().add(balance);
      info.setMoneda(Integer.valueOf(balance.getMoneda()));
      info.setNombre(balance.getNombre());
      info.setSimbolo(sistarMappingUtils.getCurrencySymbol(balance.getMoneda()));

      return info;
   }

   private void mapGetBalanceRequest(ModelMapper mapper) {
      mapper.createTypeMap(BalanceRequestDto.class, ConsultaDisponibleSistarbanc.class)
            // .addMapping(src -> sistarApiUsername, ConsultaDisponibleSistarbanc::setUsuariows)
            // .addMapping(src -> sistarApiPassword, ConsultaDisponibleSistarbanc::setPassws)
            // .addMapping(src -> sistarApiUser, ConsultaDisponibleSistarbanc::setUsuario)
            .addMapping(BalanceRequestDto::getAccount, ConsultaDisponibleSistarbanc::setCuentaExterna);
   }

   private void mapGetTransactionsRequest(ModelMapper mapper) {
      mapper.createTypeMap(TransactionsListRequestDto.class, SistarbancMDV2MOVENPERIODO.class)
            .addMapping(src -> fitoAPIUsername, SistarbancMDV2MOVENPERIODO::setUsuariows)
            .addMapping(src -> fitoAPIPassword, SistarbancMDV2MOVENPERIODO::setPassws)
            .addMappings(
               m -> m.using(dateConverter).map(TransactionsListRequestDto::getStart, SistarbancMDV2MOVENPERIODO::setDatefrom)
            )
            .addMappings(
               m -> m.using(dateConverter).map(TransactionsListRequestDto::getEnd, SistarbancMDV2MOVENPERIODO::setDateto)
            )
            .addMapping(TransactionsListRequestDto::getRealAccount, SistarbancMDV2MOVENPERIODO::setAccountnumber)
            .addMappings(m -> m.using(currencyISOtoIdConverter).map(TransactionsListRequestDto::getCurrency,
                    SistarbancMDV2MOVENPERIODO::setMoneda))
            .addMappings(m -> m.using(intToShortConverter).map(TransactionsListRequestDto::getLength,
                  SistarbancMDV2MOVENPERIODO::setCantmovsmax));
   }

   private void mapGetProductsRequest(ModelMapper mapper) {
      mapper.createTypeMap(ProductsRequestDto.class, TarjetasxCI.class).setProvider(req -> {
         TarjetasxCI ret = new TarjetasxCI();
         ret.setCliente(new Cliente());

         return ret;
      })
         .<String>addMapping(ProductsRequestDto::getDocument, (dest, value) -> dest.getCliente().setDocuNume(value))
         .addMappings(m -> m.using(conditionalProductPaisConverter).<String>map(src -> src, (dest, value) -> dest.getCliente().setPaidDocCustomer(value)))
         .addMappings(m -> m.using(stringToIntConverter).<Integer>map(ProductsRequestDto::getDocumentType, (dest, value) -> dest.getCliente().setTipoDocuCodi(value)))
         .<Long>addMapping(ProductsRequestDto::getIssuer, (dest, value) -> dest.getCliente().setPagaOtorEntiCodi(Objects.isNull(value) ? 0 : value));
   }

   private void mapCreditXCIRequest(ModelMapper mapper) {
      mapper.createTypeMap(CreditRequestDto.class, WSPagosAjustesxCI.class)
         .setProvider(provider -> {
            WSPagosAjustesxCI ret = new WSPagosAjustesxCI();
            ZoneId zoneId = ZoneId.of(sistarMappingUtils.getConfig().getLocal_timezone());
            String str = ZonedDateTime.of(LocalDateTime.now(), zoneId).toString().split("T")[0].replaceAll("-", "");
            ret.setFechaValor(str);
            return ret;
         })
         .addMapping(src -> infinitusAPIUsername, WSPagosAjustesxCI::setUsuariows)
         .addMapping(src -> infinitusAPIPassword, WSPagosAjustesxCI::setPassws)
         .addMapping(src -> "O", WSPagosAjustesxCI::setTipoWs)
         .addMappings(m -> m.using(afiliadoConverter).map(src -> src, WSPagosAjustesxCI::setAfilidado))
         .addMapping(CreditRequestDto::getProducto, WSPagosAjustesxCI::setIdProducto)
         .addMapping(CreditRequestDto::getMarca, WSPagosAjustesxCI::setMarca)
         .addMapping(CreditRequestDto::getEmisor, WSPagosAjustesxCI::setEmisor)
         .addMappings(m -> m.using(conditionalPaisConverter).map(CreditRequestDto::getBeneficiario, WSPagosAjustesxCI::setPaisDoc))
         .addMapping(src -> src.getBeneficiario().getTipoDoc(), WSPagosAjustesxCI::setTipoDoc)
         .addMapping(src -> src.getBeneficiario().getDoc(), WSPagosAjustesxCI::setNroDoc)
         .addMappings(m -> m.using(codMovimientoConverter).map(src -> src, WSPagosAjustesxCI::setCodMovimiento))
         .addMappings(m -> m.using(subCodMovimientoConverter).map(src -> src, WSPagosAjustesxCI::setSubCodMovimiento))
         .addMapping(CreditRequestDto::getRrn, WSPagosAjustesxCI::setRequestExternalId)
         .addMapping(CreditRequestDto::getMotivo, WSPagosAjustesxCI::setDescripcion)
         .addMappings(m -> m.using(currencyISOtoIdConverter).map(CreditRequestDto::getMoneda, WSPagosAjustesxCI::setMoneda))
         .<Double>addMapping(CreditRequestDto::getMonto, (dest, value) -> dest.setImporte(new BigDecimal(value)))
         .addMappings(m -> m.using(stringToIntConverter).map(CreditRequestDto::getSucursal, WSPagosAjustesxCI::setSucursal))
         .addMapping(CreditRequestDto::getComprobante, WSPagosAjustesxCI::setNumeroDeComprobante);
   }

   private void mapCreditXCtaRequest(ModelMapper mapper) {
      mapper.createTypeMap(CreditRequestDto.class, WSPagosAjustes.class)
         .setProvider(provider -> {
            WSPagosAjustes ret = new WSPagosAjustes();
            ZoneId zoneId = ZoneId.of(sistarMappingUtils.getConfig().getLocal_timezone());
            String str = ZonedDateTime.of(LocalDateTime.now(), zoneId).toString().split("T")[0].replaceAll("-", "");
            ret.setFechaValor(str);
            return ret;
         })
         .addMapping(src -> infinitusAPIUsername, WSPagosAjustes::setUsuariows)
         .addMapping(src -> infinitusAPIPassword, WSPagosAjustes::setPassws)
         .addMapping(src -> "O", WSPagosAjustes::setTipoWs)
         .addMappings(m -> m.using(afiliadoConverter).map(src -> src, WSPagosAjustes::setAfilidado))
         .addMapping(CreditRequestDto::getCuenta, WSPagosAjustes::setCuentaTarjeta)
         .addMappings(m -> m.using(codMovimientoConverter).map(src -> src, WSPagosAjustes::setCodMovimiento))
         .addMappings(m -> m.using(subCodMovimientoConverter).map(src -> src, WSPagosAjustes::setSubCodMovimiento))
         .addMapping(CreditRequestDto::getRrn, WSPagosAjustes::setIdReferencia)
         .addMapping(CreditRequestDto::getMotivo, WSPagosAjustes::setDescripcion)
         .addMappings(m -> m.using(currencyISOtoIdConverter).map(CreditRequestDto::getMoneda, WSPagosAjustes::setMoneda))
         .<Double>addMapping(CreditRequestDto::getMonto, (dest, value) -> dest.setImporte(new BigDecimal(value)))
         .addMappings(m -> m.using(stringToIntConverter).map(CreditRequestDto::getSucursal, WSPagosAjustes::setSucursal))
         .addMapping(CreditRequestDto::getComprobante, WSPagosAjustes::setNumeroDeComprobante);
   }

   private void mapC2CMultiprocRequest(ModelMapper mapper) {
      mapper.createTypeMap(TransferRequestDto.class, WSPagosAjustes.class)
         .setProvider(provider -> {
            WSPagosAjustes ret = new WSPagosAjustes();
            ZoneId zoneId = ZoneId.of(sistarMappingUtils.getConfig().getLocal_timezone());
            String str = ZonedDateTime.of(LocalDateTime.now(), zoneId).toString().split("T")[0].replaceAll("-", "");
            ret.setFechaValor(str);
            TransferRequestDto source = (TransferRequestDto) provider.getSource();

            boolean debit = source.getOrdenante() != null && source.getOrdenante().getCuenta() != null;
            String channel = source.getCanal().toLowerCase() + (debit ? "-debit": "");

            Integer code = source.getCanal() != null ? sistarMappingUtils.getCodeByChannel(channel): null;
            String subcode = source.getCanal() != null ? sistarMappingUtils.getSubCodeByChannel(channel): null;
            
            ret.setCodMovimiento(code);
            ret.setSubCodMovimiento(subcode);

            if(debit){
               ret.setCuentaTarjeta(source.getOrdenante().getCuenta());
            }else{
               ret.setCuentaTarjeta(source.getBeneficiario().getCuenta());
            }

            return ret;
         })
         .addMapping(src -> infinitusAPIUsername, WSPagosAjustes::setUsuariows)
         .addMapping(src -> infinitusAPIPassword, WSPagosAjustes::setPassws)
         .addMapping(src -> "O", WSPagosAjustes::setTipoWs)
         .addMappings(m -> m.using(afiliadoConverter2).map(src -> src, WSPagosAjustes::setAfilidado))
         .addMapping(TransferRequestDto::getRrn, WSPagosAjustes::setIdReferencia)
         .addMapping(TransferRequestDto::getMotivo, WSPagosAjustes::setDescripcion)
         .addMappings(m -> m.using(currencyISOtoIdConverter).map(TransferRequestDto::getMoneda, WSPagosAjustes::setMoneda))
         .<Double>addMapping(TransferRequestDto::getMonto, (dest, value) -> dest.setImporte(new BigDecimal(value)))
         .addMapping(TransferRequestDto::getSucursal, WSPagosAjustes::setSucursal);
   }

   private Integer getAfiliado(CreditRequestDto src) {
      if (Objects.isNull(src)) {
         return null;
      }
      final int sucursal = NumberUtils.toInt(src.getSucursal());
      final int emisor = ObjectUtils.defaultIfNull(src.getEmisor(), 0);
      final int moneda = ObjectUtils.defaultIfNull(src.getMoneda(), 840);
      return getCode(sucursal, emisor, moneda);
   }

   private Integer getCode(final int sucursal, final int emisor, final int moneda) {
      String formato;
      switch (moneda) {
         case 858:
            formato = "00%02d%04d";
            break;
         case 840:
            formato = "9%02d%05d";
            break;
         default:
            return null;
      }
      return NumberUtils.toInt(String.format(formato, emisor, sucursal));
   }

   private Integer getAfiliado(TransferRequestDto src) {
      if (Objects.isNull(src)) {
         return null;
      }
      final int sucursal = ObjectUtils.defaultIfNull(src.getSucursal(), sistarMappingUtils.getConfig().getDefault_branch_office());
      final int emisor = ObjectUtils.defaultIfNull(src.getOrdenante().getEmisor(), 
                         ObjectUtils.defaultIfNull(src.getBeneficiario().getEmisor(), 0L)).intValue();
      final int moneda = ObjectUtils.defaultIfNull(src.getMoneda(), 858);
      return getCode(sucursal, emisor, moneda);
   }

   private Integer getAfiliado(ReverseRequestDto src) {
      if (Objects.isNull(src)) {
         return null;
      }
      final int sucursal = ObjectUtils.defaultIfNull(src.getSucursal(), sistarMappingUtils.getConfig().getDefault_branch_office());
      final int emisor = ObjectUtils.defaultIfNull(src.getEmisor(), 0L).intValue();
      final int moneda = ObjectUtils.defaultIfNull(src.getMoneda(), 858);
      return getCode(sucursal, emisor, moneda);
   }

   private void mapCreditReverseRequest(ModelMapper mapper) {
      mapper.createTypeMap(ReverseRequestDto.class, WSPagosAjustesxCI.class)
         .setProvider(provider -> {
            WSPagosAjustesxCI ret = new WSPagosAjustesxCI();
            ZoneId zoneId = ZoneId.of(sistarMappingUtils.getConfig().getLocal_timezone());
            String str = ZonedDateTime.of(LocalDateTime.now(), zoneId).toString().split("T")[0].replaceAll("-", "");
            ret.setFechaValor(str);
            return ret;
         })
         .addMapping(src -> infinitusAPIUsername, WSPagosAjustesxCI::setUsuariows)
         .addMapping(src -> infinitusAPIPassword, WSPagosAjustesxCI::setPassws)
         .addMapping(src -> "R", WSPagosAjustesxCI::setTipoWs)
         .addMapping(ReverseRequestDto::getEmisor, WSPagosAjustesxCI::setEmisor)
         .addMapping(ReverseRequestDto::getToReverseRrn, WSPagosAjustesxCI::setRequestExternalIdARevertir)
         .addMapping(ReverseRequestDto::getRrn, WSPagosAjustesxCI::setRequestExternalId);

      mapper.createTypeMap(ReverseRequestDto.class, WSPagosAjustes.class)
         .setProvider(provider -> {
            WSPagosAjustes ret = new WSPagosAjustes();
            ZoneId zoneId = ZoneId.of(sistarMappingUtils.getConfig().getLocal_timezone());
            String str = ZonedDateTime.of(LocalDateTime.now(), zoneId).toString().split("T")[0].replaceAll("-", "");
            ret.setFechaValor(str);
            return ret;
         })
         .addMapping(src -> infinitusAPIUsername, WSPagosAjustes::setUsuariows)
         .addMapping(src -> infinitusAPIPassword, WSPagosAjustes::setPassws)
         .addMapping(src -> "R", WSPagosAjustes::setTipoWs)
         .addMapping(ReverseRequestDto::getCuenta, WSPagosAjustes::setCuentaTarjeta)
         .addMappings(m -> m.using(currencyISOtoIdConverter).map(ReverseRequestDto::getMoneda, WSPagosAjustes::setMoneda))
         .addMappings(m -> m.using(codMovimientoConverter3).map(src -> src, WSPagosAjustes::setCodMovimiento))
         .addMappings(m -> m.using(subCodMovimientoConverter3).map(src -> src, WSPagosAjustes::setSubCodMovimiento))
         .addMapping(ReverseRequestDto::getSucursal, WSPagosAjustes::setSucursal)
         .addMappings(m -> m.using(afiliadoConverter3).map(src -> src, WSPagosAjustes::setAfilidado))
         .addMapping(ReverseRequestDto::getToReverseRrn, WSPagosAjustes::setIdReferencia);
   }

   private void mapNewCardResponse(ModelMapper mapper) {
      mapper.createTypeMap(SistarbancMDF2ABMCUENTATARJETAResponse.class, NewCardResponseDto.class)
            .addMappings(m -> m.using(resultCodeStringToSuccessFlagConverter)
                  .map(SistarbancMDF2ABMCUENTATARJETAResponse::getCoderror, NewCardResponseDto::setSuccess))
            .addMapping(SistarbancMDF2ABMCUENTATARJETAResponse::getExternalaccountnumber,
                  NewCardResponseDto::setRealAccount);

      mapper.createTypeMap(ResultInnominada.class, NewCardResponseDto.class)
              .addMappings(m -> m.using(resultCodeLongToSuccessFlagConverter)
                      .map(ResultInnominada::getErrorCode, NewCardResponseDto::setSuccess))
              .addMapping(ResultInnominada::getRelaCuentaNume,
                      NewCardResponseDto::setRealAccount);
   }

   private void mapGetProductsResponse(ModelMapper mapper) {
      mapper.createTypeMap(TarjetaCIBanco.class, Product.class)
      .addMapping(TarjetaCIBanco::getCodigo_Tipo_Producto, Product::setCodigo)
      .addMapping(TarjetaCIBanco::getDescripcion_Tipo_Producto, Product::setNombre)
      .addMappings(m -> m.using(brandNameConverter).map(TarjetaCIBanco::getBanco_de_la_Tarjeta, Product::setSello))
      .addMappings(m -> m.using(gafConverter).map(src -> src, Product::setGrupoAfinidad));
      
      mapper.createTypeMap(TarjetaCIBanco.class, Card.class)
      .addMappings(m -> m.using(cardStatusConverter).map(src -> src, Card::setEstado))
      .addMapping(TarjetaCIBanco::getNombre, Card::setName1)
      .addMapping(TarjetaCIBanco::getApellido_1, Card::setLastName1)
      .addMappings(m -> m.using(lastFourDigitsConverter).map(TarjetaCIBanco::getTarjeta, Card::setUltimosCuatro))
      .addMappings(m -> m.using(lastDayOfMonthConverter).map(TarjetaCIBanco::getVencimiento_AAAAMM, Card::setExpiracion));
      
      mapper.createTypeMap(TarjetaCIBanco.class, ProductInfo.class)
      .setProvider(provider -> {
         if(provider.getSource() != null){
            TarjetaCIBanco data = (TarjetaCIBanco) provider.getSource();
            ProductInfo ret = new ProductInfo();
            ret.setCuenta(data.getCuenta());
            if(NumberUtils.toInt(data.getCodigoError()) == 0){
               ret.setTarjetas(Collections.singletonList(mapper.map(data, Card.class)));
            }else{
               ret.setTarjetas(Collections.emptyList());
            }
            
            return ret;
         }
         return null;
      })
      .addMapping(src -> src, ProductInfo::setProducto);
   }

   private void mapNewCardRequest(ModelMapper mapper) {
      mapper.createTypeMap(NewCardRequestDto.class, SistarbancMDF2ABMCUENTATARJETA.class).setProvider(incomingReq -> {
         SistarbancMDF2ABMCUENTATARJETA sistarReq = new SistarbancMDF2ABMCUENTATARJETA();
         sistarReq.setAccount(new AccountDTO());
         sistarReq.getAccount().setPrincipalCustomer(new ArrayOfCustomerDTO());
         if (Objects.nonNull(incomingReq.getSource())) {
            NewCardRequestDto reqDto = (NewCardRequestDto) incomingReq.getSource();
            // Creo el representante si está definido en la solicitud
            if (Objects.nonNull(reqDto.getRepresentante()) && config.isInclude_representative_info()) {
               sistarReq.getAccount().getPrincipalCustomer().getCustomerDTO()
                     .add(mapper.map(reqDto.getRepresentante(), CustomerDTO.class));
            }
            // Creo el beneficiario
            sistarReq.getAccount().getPrincipalCustomer().getCustomerDTO()
                  .add(mapper.map(reqDto.getBeneficiario(), CustomerDTO.class));
            if(sistarReq.getAccount().getPrincipalCustomer().getCustomerDTO().size() > 1){
              CustomerDTO representative = sistarReq.getAccount().getPrincipalCustomer().getCustomerDTO().get(0);
              representative.setTrjRel(REPRESENTATIVE_RELATION_CODE);

              CustomerDTO underage = sistarReq.getAccount().getPrincipalCustomer().getCustomerDTO().get(1);
              underage.setTrjRel(UNDERAGE_RELATION_CODE);
            }
         }
         return sistarReq;
      }).addMapping(src -> fitoAPIUsername, SistarbancMDF2ABMCUENTATARJETA::setUsuariows)
            .addMapping(src -> sistarApiUserCode, SistarbancMDF2ABMCUENTATARJETA::setUsuario)
            .addMapping(src -> fitoAPIPassword, SistarbancMDF2ABMCUENTATARJETA::setPassws)
            .addMapping(src -> OPERATION_CODE_ALTA, SistarbancMDF2ABMCUENTATARJETA::setOperationcode)
            .setPostConverter(remainingFieldsConverter);
   }

   private void mapAddress(ModelMapper mapper) {
      mapper.createTypeMap(DireccionDto.class, AddressDTO.class)
            .addMapping(DireccionDto::getCodigoPostal, AddressDTO::setZipCode)
            .addMappings(m -> m.using(departamentoConverter).map(src -> src, AddressDTO::setGeoStateCode))
            .addMappings(m -> m.using(localidadStringConverter).map(src -> src, AddressDTO::setCityCode))
            .addMapping(DireccionDto::getPais, AddressDTO::setCountryCode)
            .addMappings(m -> m.using(boolConverter).map(DireccionDto::getResidente, AddressDTO::setResidente))
            .addMappings(m -> m.using(addressConverter).map(src -> src, AddressDTO::setAddress));
   }

   private void mapBeneficiary(ModelMapper mapper) {
      mapper.createTypeMap(BeneficiarioDto.class, CustomerDTO.class)
            .addMappings(m -> m.using(toUpperCase).map(BeneficiarioDto::getNombre, CustomerDTO::setNombre))
            .addMappings(m -> m.using(toUpperCase).map(BeneficiarioDto::getNombre2, CustomerDTO::setNombre2))
            .addMappings(m -> m.using(toUpperCase).map(BeneficiarioDto::getApellido, CustomerDTO::setApellido))
            .addMappings(m -> m.using(toUpperCase).map(BeneficiarioDto::getApellido2, CustomerDTO::setApellido2))
            .addMapping(BeneficiarioDto::getNacionalidad, CustomerDTO::setPaIdNacCustomer)
            .addMapping(BeneficiarioDto::getFechaNacimiento, CustomerDTO::setBirthday)
            .addMapping(BeneficiarioDto::getDireccion, CustomerDTO::setPhisicalAddress)
            .addMapping(BeneficiarioDto::getEstadoCivil, CustomerDTO::setCivId)
            .addMappings(m -> m.using(toUpperCase).map(BeneficiarioDto::getRelacion, CustomerDTO::setParentesco))
            .<Short>addMapping(src -> 100, (dest, value) -> dest.getProduct().setCreditLimitPorce(value))
            .<String>addMapping(src -> "1", (dest, value) -> dest.getProduct().setPreId(value))
            .<String>addMapping(src -> "S", (dest, value) -> dest.getProduct().setRenovAutom(value))
            .addMapping(src -> HOLDER_RELATION_CODE, CustomerDTO::setTrjRel)
            .addMappings(m -> m.using(embossingNameConverter).map(str -> (str), CustomerDTO::setNombreEmbozado))
            .addMappings(
                  m -> m.using(docTypeConverter).map(src -> src.getDoc().getTipoDoc(), CustomerDTO::setDocumentType))
            .addMappings(m -> m.using(toUpperCase).map(src -> src.getDoc().getNum(), CustomerDTO::setDocumentNumber))
            .addMappings(m -> m.map(src -> src.getDoc().getPais(), CustomerDTO::setPaIdDocCustomer))
            .addMappings(m -> m.using(genderConverter).map(BeneficiarioDto::getSexo, CustomerDTO::setCodSex))
            .addMappings(
                  m -> m.using(landlinePhoneConverter).map(BeneficiarioDto::getTelefonos, CustomerDTO::setPhoneNumber))
            .addMappings(
                  m -> m.using(mobilePhoneConverter).map(BeneficiarioDto::getTelefonos, CustomerDTO::setCellNumber));
   }

   private final Converter<IdentificationDto, String> conditionalPaisConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      IdentificationDto data = ctx.getSource();
      String paddedCode = StringUtils.leftPad(data.getPais(), 3, "0");
      return config.getDoc_type_without_country().contains(data.getTipoDoc()) ? "000" : paddedCode;
   };

   private final Converter<ProductsRequestDto, String> conditionalProductPaisConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      ProductsRequestDto data = ctx.getSource();
      String paddedCode = StringUtils.leftPad(data.getCountry(), 3, "0");
      return config.getDoc_type_without_country().contains(data.getDocumentType()) ? "000" : paddedCode;
   };

   private final Converter<Object, String> toStringConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      return String.valueOf(ctx.getSource());
   };

   private final Converter<String, Boolean> resultCodeStringToSuccessFlagConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      } else if(ctx.getSource() != null){
         return NumberUtils.toInt(ctx.getSource(), 0) == 0;
      }
      return null;
   };

   private final Converter<Long, Boolean> resultCodeLongToSuccessFlagConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      return ctx.getSource() == 0;
   };

   private final Converter<String, String> resultCodeStringToItc = ctx -> {
      if(ctx.getSource() == null){
         return null;
      } else if(ctx.getSource() != null && NumberUtils.toInt(ctx.getSource(), 0) == 0){
         return "R200.40.01";
      }
      return null;
   };

   private final Converter<Long, String> resultCodeLongToItc = ctx -> {
      if(ctx.getSource() == null){
         return null;
      } else if(ctx.getSource() == 0){
         return "R200.40.01";
      }
      return null;
   };

   // Convert from 20210522 to 2021-05-22
   private final Converter<String, String> addSeparatorToDateConverter = ctx -> {
       if(ctx.getSource() == null){
           return null;
       }
       if(ctx.getSource().matches("\\d{8}")){
           String date = ctx.getSource();
           return String.format("%s-%s-%s", date.substring(0, 4), date.substring(4, 6), date.substring(6));
       }
       return ctx.getSource();
   };

   // Convert from 101234 to 10:12:34
   private final Converter<String, String> addSeparatorToTimeConverter = ctx -> {
       if(ctx.getSource() == null){
           return null;
       }
       if(ctx.getSource().matches("\\d{6}")){
           String date = ctx.getSource();
           return String.format("%s:%s:%s", date.substring(0, 2), date.substring(2, 4), date.substring(4));
       }
       return ctx.getSource();
   };

   private final Converter<String, Integer> stringToIntConverter = ctx -> NumberUtils.toInt(ctx.getSource(), 0);

   private final Converter<String, Long> stringToLongConverter = ctx -> NumberUtils.toLong(ctx.getSource(), 0);

   private final Converter<String, BigDecimal> stringToBigDecimalConverter = ctx -> NumberUtils.toScaledBigDecimal(ctx.getSource());

   private final Converter<Integer, Short> intToShortConverter = ctx -> ObjectUtils
         .defaultIfNull(ctx.getSource(), 0).shortValue();

   private final Converter<XMLGregorianCalendar, Date> calendarToDateConverter = ctx -> {
      if(Objects.nonNull(ctx.getSource())){
         XMLGregorianCalendar cal = ctx.getSource();
         return cal.toGregorianCalendar().getTime();
      }
      return null;
   };

  private final Converter<String, XMLGregorianCalendar> dateConverter = ctx -> {
    if(ctx.getSource() == null){
      return null;
    }
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(ctx.getSource());
    } catch (DatatypeConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  };

   private final Converter<String, XMLGregorianCalendar> dateTimeConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      try {
        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
            .appendPattern("[yyyyMMdd]" +
                "[yyyy-MM-dd]" +
                "[yyyy/MM/dd]")
            .appendOptional(DateTimeFormatter.ofPattern("'T'"))
            .appendOptional(DateTimeFormatter.ofPattern("' '"))
            .appendOptional(DateTimeFormatter.ofPattern("HH:mm:ss"))
            .appendOptional(DateTimeFormatter.ofPattern(".SSSSSS"))
            .appendOptional(DateTimeFormatter.ofPattern("[z][Z]"))
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();
        ZonedDateTime dateTime = ZonedDateTime.parse(ctx.getSource(), fmt);
//        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime zonedDT = dateTime.withZoneSameInstant(ZoneId.of(config.getLocal_timezone()));
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(
            GregorianCalendar.from(zonedDT)
        );
      } catch (DatatypeConfigurationException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
   };
   
   private final Converter<String, String> cleanAuthorizationCodeConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
     return ctx.getSource().replaceAll("\\.00$", "").trim();
   };

   private final Converter<MOVENPERIODOITEM, Integer> statusToVoidCountConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      int statueType = ctx.getSource().getESTADOTIPO();
      int statusCode = ctx.getSource().getESTADOCODI();
      short code = ctx.getSource().getCODTX();
      short subcode = ctx.getSource().getSUBCODTX();
      boolean isNeg = ctx.getSource().getCARDHOLDERAMOUNT() < 0 || ctx.getSource().getSETTLEMENTAMOUNT() < 0 || ctx.getSource().getORIGINALAMOUNT() < 0;
      // TODO parametrizar
      Boolean isVoid = (statueType == 8 && (ctx.getSource().getESTADOCODI() == 3 || statusCode == 7)) ||
         (statueType == 6 && statusCode == 3);
      return isVoid ? 1: 0;
   };
   private final Converter<MOVENPERIODOITEM, Integer> statusToReversalCountConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      int statueType = ctx.getSource().getESTADOTIPO();
      int statusCode = ctx.getSource().getESTADOCODI();
      short code = ctx.getSource().getCODTX();
      short subcode = ctx.getSource().getSUBCODTX();
      boolean isNeg = ctx.getSource().getCARDHOLDERAMOUNT() < 0 || ctx.getSource().getSETTLEMENTAMOUNT() < 0 || ctx.getSource().getORIGINALAMOUNT() < 0;
      // TODO parametrizar
      Boolean isReversal = (statueType == 8 && (ctx.getSource().getESTADOCODI() == 3 || statusCode == 7)) ||
         (statueType == 6 && statusCode == 3) ||
         (code == 11 && subcode == 27 && isNeg);
      return isReversal ? 1: 0;
   };
    private final Converter<MOVENPERIODOITEM, BigDecimal> amountReversalConverter = ctx -> {
        if (Objects.isNull(ctx.getSource())) {
            return null;
        }

        MOVENPERIODOITEM mov = ctx.getSource();
        boolean isNeg = ctx.getSource().getCARDHOLDERAMOUNT() < 0 || ctx.getSource().getSETTLEMENTAMOUNT() < 0 || ctx.getSource().getORIGINALAMOUNT() < 0;
        if (ctx.getSource().getCODTX() == 11 && ctx.getSource().getSUBCODTX() == 27 && isNeg) {
             return BigDecimal.valueOf(ctx.getSource().getCARDHOLDERAMOUNT() * -1);
        }

        return BigDecimal.valueOf(ctx.getSource().getCARDHOLDERAMOUNT());
    };

   private final Converter<Object, String> toIsoCurrencyConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      String originalCode = String.valueOf(ctx.getSource());
      if(originalCode.matches("^[0-9]$")){
         return sistarMappingUtils.getCurrencyISOFromCurrencyId(originalCode);
      }
      return originalCode;
   };

   private final Converter<Object, Long> toIsoCurrencyConverter1 = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      String originalCode = String.valueOf(ctx.getSource());
      if(originalCode.matches("^[0-9]$")){
         return NumberUtils.toLong(sistarMappingUtils.getCurrencyISOFromCurrencyId(originalCode), 0);
      }
      return Long.parseLong(originalCode);
   };

   private final Converter<XMLGregorianCalendar, String> dateToStringDateConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      XMLGregorianCalendar date = ctx.getSource();
     return date.toXMLFormat().split("T")[0];
   };

   private final Converter<XMLGregorianCalendar, String> dateToStringDateTimeConverter = ctx -> {
     if(ctx.getSource() == null) {
       return null;
     }
     XMLGregorianCalendar date = ctx.getSource();
     return date.toXMLFormat().replace("T" , " ");
   };

   private void mapNewCardUnnamedRequest(ModelMapper mapper) {
      mapper
            .createTypeMap(NewCardRequestDto.class, CrearTarjetaPrepagaInnominada.class)
            .addMappings(m -> m.using(innominadaConverter).map(str -> (str), CrearTarjetaPrepagaInnominada::setSolicitudPrepagaInnominada));
   }

   private void mapPersonalAddress(ModelMapper mapper) {
      mapper
            .createTypeMap(DireccionDto.class, Direccion.class)
            .addMappings(m -> m.using(toUpperCase).map(DireccionDto::getCalle, Direccion::setDomiCalle))
            .addMapping(DireccionDto::getPuerta, Direccion::setDomiPuerta)
            .addMappings(m -> m.using(toUpperCase).map(DireccionDto::getApto, Direccion::setDomiRefe))
            .addMapping(DireccionDto::getPiso, Direccion::setDomiPiso)
            .addMappings(m -> m.using(paisConverter).map(DireccionDto::getPais, Direccion::setPaisCodi))
            .addMappings(m -> m.using(localidadConverter).map(src -> src, Direccion::setDomiDepar))
            .addMappings(m -> m.using(departamentoIntegerConverter).map(src -> src, Direccion::setProvinCodi))
            .addMapping(DireccionDto::getCodigoPostal, Direccion::setDomiPostalCodi)
            .addMappings(m -> m.using(paisDescConverter).map(DireccionDto::getPais, Direccion::setPaisDescri))
            .addMappings(m -> m.using(toUpperCase).map(DireccionDto::getAmplicacionCalle, Direccion::setDomiComentario));
   }

   private final Converter<String, String> toUpperCase = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      return StringUtils.stripAccents(ctx.getSource().toUpperCase());
   };

   private final Converter<CreditRequestDto, Integer> afiliadoConverter = ctx -> getAfiliado(ctx.getSource());

   private final Converter<TransferRequestDto, Integer> afiliadoConverter2 = ctx -> getAfiliado(ctx.getSource());

   private final Converter<ReverseRequestDto, Integer> afiliadoConverter3 = ctx -> getAfiliado(ctx.getSource());

   private final Converter<NewCardRequestDto, SolicitudPrepagaInnominada> innominadaConverter = ctx -> {
      NewCardRequestDto src = ctx.getSource();
      SolicitudPrepagaInnominada dest = new SolicitudPrepagaInnominada();
      dest.setUsuaCodi(infinitusAPIUsername);
      dest.setPassword(infinitusAPIPassword);
      dest.setNumSobre(src.getNumeroDeSobre());
      if (Objects.nonNull(src.getBeneficiario())) {
         BeneficiarioDto benef = src.getBeneficiario();
         dest.setTipoDocuCodi(Integer.valueOf(benef.getDoc().getTipoDoc()));
         dest.setPersoDocuNume(benef.getDoc().getNum());
         dest.setPersoNombre(benef.getNombre().trim().toUpperCase());
         
         // trunca apellido a máximo tamaño configurado
         String lastName = benef.getApellido().trim().toUpperCase();
         lastName = lastName.substring(0, Math.min(lastName.length(), config.getLastName_max_length()));
         lastName = StringUtils.stripAccents(lastName);
         
         dest.setPersoApe(lastName);
         
         // trunca nombre a máximo tamaño configurado
         String name = benef.getNombre().trim().toUpperCase();
         name = name.substring(0, Math.min(name.length(), config.getLastName_max_length()));
         name = StringUtils.stripAccents(name);
         
         dest.setPersoNombre(name);
         
         if (Objects.nonNull(benef.getTelefonos()) && !benef.getTelefonos().isEmpty()) {
            dest.setTeleNume(benef.getTelefonos().get(0).getNum());
         }
         dest.setPersoEMail(benef.getEmail());
         dest.setPersoSexo(sistarMappingUtils.getGender(benef.getSexo()));
         dest.setPersoNacimFecha(sistarMappingUtils.dateFromYyyymmdd(benef.getFechaNacimiento()));

         final Direccion dir = modelMapper.map(src.getBeneficiario().getDireccion(), Direccion.class);
         // trunca direccion a maximo tamaño configurado
         dir.setDomiCalle(dir.getDomiCalle().substring(0, Math.min(dir.getDomiCalle().length(), config.getAddr_max_length())));
         
         dest.setDireccionParticular(dir);
         dest.setPaisCodiNacionalidad(sistarMappingUtils.getCodPais(benef.getNacionalidad()));
      }
      return dest;
   };

   private void mapCardUnnamedReplacementRequest(ModelMapper mapper) {
      mapper
         .createTypeMap(ReemplazoRequestDto.class, ReemplazoTarjetaPrepagaInnominada.class)
         .addMappings(
               m -> m.using(replacementByAccountConverter).map(str -> (str), ReemplazoTarjetaPrepagaInnominada::setReemplazoTarjetaPrepagaInnominada));

      mapper
         .createTypeMap(ReemplazoRequestDto.class, ReemplazoTarjetaPrepagaInnominadaXCi.class)
         .addMappings(
               m -> m.using(replacementByIDConverter).map(str -> (str), ReemplazoTarjetaPrepagaInnominadaXCi::setReemplazoTarjetaPrepagaInnominadaXCi));
   }

   private void mapCardUnnamedRenew(ModelMapper mapper){
      mapper
         .createTypeMap(ReemplazoRequestDto.class, RenovacionTarjetaPrepagaInnominadaXCi.class)
         .addMappings(
               m -> m.using(replacementByIDConverter).map(str -> (str), RenovacionTarjetaPrepagaInnominadaXCi::setRenovacionTarjetaPrepagaInnominadaXCi));
   }

    private void mapAccountVpsRequest(ModelMapper mapper) {
        mapper.createTypeMap(AccountVpsRequestDto.class, ExceptionFraudeAlta.class)
                .addMappings(m -> m.using(vpsExceptionFraudeAltaConverter).map(src -> src, ExceptionFraudeAlta::setExceptionFraudeAlta));
    }

    private void mapVpsToExceptionFraude2Request(ModelMapper mapper) {
        mapper.createTypeMap(AccountVpsRequestDto.class, ExceptionFraudeAlta2.class)
                .addMapping(src -> infinitusAPIUsername, ExceptionFraudeAlta2::setUsuaCodi)
                .addMapping(src -> infinitusAPIPassword, ExceptionFraudeAlta2::setPassword)
                .addMapping(src -> infinitusAPIUsername, ExceptionFraudeAlta2::setUsuario)
                .addMapping(AccountVpsRequestDto::getRrn, ExceptionFraudeAlta2::setRequestId)
                .addMapping(AccountVpsRequestDto::getRrn, ExceptionFraudeAlta2::setRequestExternalId)
                .addMapping(AccountVpsRequestDto::getMarca, ExceptionFraudeAlta2::setMarcaCodi)
                .addMapping(AccountVpsRequestDto::getEmisor, ExceptionFraudeAlta2::setPagaOtorEntiCodi)
                .addMapping(src -> 3, ExceptionFraudeAlta2::setIdenMetodo)
                .addMapping(AccountVpsRequestDto::getRealAccount, ExceptionFraudeAlta2::setIdenNume)
                .addMapping(src -> 0, ExceptionFraudeAlta2::setEstadoCodi)
                .addMappings(m -> m.using(currencyISOtoIdConverter).<Short>map(AccountVpsRequestDto::getMoneda, (dest, value) -> dest.setMoneCodi(Short.toUnsignedInt(value))))
                .addMappings(m -> m.using(dateTimeConverter).map(AccountVpsRequestDto::getInicio, ExceptionFraudeAlta2::setFechaDesde))
                .addMappings(m -> m.using(dateTimeConverter).map(AccountVpsRequestDto::getFin, ExceptionFraudeAlta2::setFechaHasta))
                .addMapping(AccountVpsRequestDto::getMotivo, ExceptionFraudeAlta2::setObservacion);
    }

    private final Converter<AccountVpsRequestDto, ExceptionFraudeAlta2> vpsExceptionFraudeAltaConverter = ctx -> {
        AccountVpsRequestDto src = ctx.getSource();
        if(src != null){
          return modelMapper.map(src, ExceptionFraudeAlta2.class);
        }
        return null;
    };

    private void mapExceptionFraudeAltaResponse(ModelMapper mapper) {
        mapper.createTypeMap(ExcepcionFraudeAltaResult.class, AccountVPSResponseDto.class)
            .addMapping(src -> src, AccountVPSResponseDto::setInfo)
            .addMappings(m -> m.using(resultCodeLongToSuccessFlagConverter)
                .map(ExcepcionFraudeAltaResult::getErrorCode, AccountVPSResponseDto::setSuccess)
            );

        mapper.createTypeMap(ExcepcionFraudeAltaResult.class, AccountVPSResponseDto.Info.class)
                .addMapping(ExcepcionFraudeAltaResult::getCuenta, AccountVPSResponseDto.Info::setRealAccount);
    }

   private void mapAccountVpsRequestToConsul(ModelMapper mapper) {
      mapper.createTypeMap(AccountVpsRequestDto.class, ExceptionFraudeConsul.class)
              .addMappings(m -> m.using(vpsExceptionFraudeConsulConverter).map(src -> src, ExceptionFraudeConsul::setExceptionFraudeConsul));
   }

   private void mapVpsToExceptionFraudeConsul2Request(ModelMapper mapper) {
      mapper.createTypeMap(AccountVpsRequestDto.class, ExceptionFraudeConsul2.class)
          .addMapping(src -> infinitusAPIUsername, ExceptionFraudeConsul2::setUsuaCodi)
          .addMapping(src -> infinitusAPIPassword, ExceptionFraudeConsul2::setPassword)
          .addMapping(src -> infinitusAPIUsername, ExceptionFraudeConsul2::setUsuario)
          .addMapping(AccountVpsRequestDto::getMarca, ExceptionFraudeConsul2::setMarcaCodi)
          .addMapping(AccountVpsRequestDto::getEmisor, ExceptionFraudeConsul2::setPagaOtorEntiCodi)
          .addMapping(src -> 3, ExceptionFraudeConsul2::setIdenMetodo)
          .addMapping(AccountVpsRequestDto::getRealAccount, ExceptionFraudeConsul2::setIdenNume)
          .addMapping(AccountVpsRequestDto::getMotivo, ExceptionFraudeConsul2::setObservacion);
   }

   private final Converter<AccountVpsRequestDto, ExceptionFraudeConsul2> vpsExceptionFraudeConsulConverter = ctx -> {
      AccountVpsRequestDto src = ctx.getSource();
      if(src != null){
        return modelMapper.map(src, ExceptionFraudeConsul2.class);
      }
      return null;
   };

   private void mapExceptionFraudeConsulResponse(ModelMapper mapper) {

      Converter<ExcepcionFraudeConsulResult, Boolean> resultCodeToSuccessFlagConverter = ctx -> {
         if(ctx.getSource() == null){
            return null;
         }
         ExcepcionFraudeConsulResult src = ctx.getSource();
         return src.getErrorCode() == 0 || src.getErrorDescription().matches(".*no hay datos");
      };
      mapper.createTypeMap(ExcepcionFraudeConsulResult.class, AccountVPSResponseDto.class)
          .addMapping(src -> src, AccountVPSResponseDto::setInfo)
          .addMappings(m -> m.using(resultCodeToSuccessFlagConverter)
              .map(src -> src, AccountVPSResponseDto::setSuccess)
          );

      mapper.createTypeMap(ExcepcionFraudeConsulResult.class, AccountVPSResponseDto.Info.class)
          .setProvider(provider -> {
            if(provider.getSource() == null){
              return null;
            }
            ExcepcionFraudeConsulResult src = (ExcepcionFraudeConsulResult) provider.getSource();
            List<ExceptionFraude> res = new ArrayList<>();
            Integer size = Optional.of(src)
                .map(ExcepcionFraudeConsulResult::getListaExcepciones)
                .map(ArrayOfEFConsul::getEFConsul)
                .map(List::size)
                .orElse(0);

            if(size > 0){
              for(EFConsul exception: src.getListaExcepciones().getEFConsul()){
                ExceptionFraude item = modelMapper.map(exception, ExceptionFraude.class);
                res.add(item);
              }
            }
            AccountVPSResponseDto.Info ret = new AccountVPSResponseDto.Info();
            ret.setExceptionFraude(res);

            return ret;
          })
          .addMapping(ExcepcionFraudeConsulResult::getCuenta, AccountVPSResponseDto.Info::setRealAccount);
   }

    private void mapEFConsul(ModelMapper mapper) {
        mapper.createTypeMap(EFConsul.class, ExceptionFraude.class)
            .addMappings(m -> m.using(dateToStringDateTimeConverter).map(EFConsul::getFechaDesde, ExceptionFraude::setFrom))
            .addMappings(m -> m.using(dateToStringDateTimeConverter).map(EFConsul::getFechaHasta, ExceptionFraude::setTo))
            .addMapping(EFConsul::getTefId, ExceptionFraude::setId)
            .addMappings(m -> m.using(toIsoCurrencyConverter1).map(EFConsul::getMoneCodi, ExceptionFraude::setCurrency));
    }

    private void mapAccountVpsModiRequest(ModelMapper mapper) {
        mapper.createTypeMap(AccountVpsRequestDto.class, ExceptionFraudeModi.class)
                .addMappings(m -> m.using(vpsExceptionFraudeModiConverter).map(src -> src, ExceptionFraudeModi::setExceptionFraudeModificacion));
    }

    private void mapVpsToExceptionFraudeModificacionRequest(ModelMapper mapper) {
        mapper.createTypeMap(AccountVpsRequestDto.class, ExceptionFraudeModificacion.class)
                .addMapping(src -> infinitusAPIUsername, ExceptionFraudeModificacion::setUsuaCodi)
                .addMapping(src -> infinitusAPIPassword, ExceptionFraudeModificacion::setPassword)
                .addMapping(src -> sistarApiUserCode, ExceptionFraudeModificacion::setUsuario)
                .addMapping(AccountVpsRequestDto::getRrn, ExceptionFraudeModificacion::setRequestId)
                .addMapping(AccountVpsRequestDto::getRrn, ExceptionFraudeModificacion::setRequestExternalId)
                .addMapping(AccountVpsRequestDto::getId, ExceptionFraudeModificacion::setTefId)
                .addMapping(AccountVpsRequestDto::getEnable, (dest, value) -> dest.setEstadoCodi(value != null && (Boolean) value ? sistarMappingUtils.getConfig().getSecurity_exception_disable_code(): 0))
                .addMapping(AccountVpsRequestDto::getMarca, ExceptionFraudeModificacion::setMarcaCodi)
                .addMapping(AccountVpsRequestDto::getEmisor, ExceptionFraudeModificacion::setPagaOtorEntiCodi)
                .addMapping(src -> 3, ExceptionFraudeModificacion::setIdenMetodo)
                .addMapping(AccountVpsRequestDto::getRealAccount, ExceptionFraudeModificacion::setIdenNume)
                .addMapping(src -> 0, ExceptionFraudeModificacion::setIdenNumeTipo)
                .addMappings(m -> m.using(dateTimeConverter).map(AccountVpsRequestDto::getInicio, ExceptionFraudeModificacion::setFechaDesde))
                .addMappings(m -> m.using(dateTimeConverter).map(AccountVpsRequestDto::getFin, ExceptionFraudeModificacion::setFechaHasta))
                .addMapping(AccountVpsRequestDto::getMotivo, ExceptionFraudeModificacion::setObservacion);
    }

    private final Converter<AccountVpsRequestDto, ExceptionFraudeModificacion> vpsExceptionFraudeModiConverter = ctx -> {
        AccountVpsRequestDto src = ctx.getSource();
        if(src != null){
            return modelMapper.map(src, ExceptionFraudeModificacion.class);
        }
        return null;
    };

    private void mapExceptionFraudeModiResponse(ModelMapper mapper) {
        mapper.createTypeMap(Result.class, AccountVPSResponseDto.class)
            .addMapping(src -> src, AccountVPSResponseDto::setInfo)
            .addMappings(m -> m.using(resultCodeLongToSuccessFlagConverter)
                .map(Result::getErrorCode, AccountVPSResponseDto::setSuccess)
            );

        mapper.createTypeMap(Result.class, AccountVPSResponseDto.Info.class)
            .addMapping(Result::getCuenta, AccountVPSResponseDto.Info::setRealAccount);
    }

   private final Converter<TransferUpdateRequestDto, MDConfirmacion> c2bConfirmConverter = ctx -> {
      MDConfirmacion ret = new MDConfirmacion();
      TransferUpdateRequestDto src = ctx.getSource();
      if(src != null){
         ret.setIDTRANSACCION(Long.parseLong(src.getRrn()));
         ret.setNROCUENTA(Long.parseLong(src.getCuenta()));
         return ret;
      }

      return null;
   };

   private final Converter<TransferUpdateRequestDto, MDReverso> c2bReverseConverter = ctx -> {
      MDReverso ret = new MDReverso();
      TransferUpdateRequestDto src = ctx.getSource();
      if(src != null){
         ret.setIDTRANSACCION(Long.parseLong(src.getRrn()));
         ret.setNROCUENTA(Long.parseLong(src.getCuenta()));
         return ret;
      }

      return null;
   };

   private final Converter<TransferRequestDto, TransferenciaC2C2> c2c2TransferConverter = ctx -> {
      TransferenciaC2C2 ret = new TransferenciaC2C2();
      TransferRequestDto src = ctx.getSource();
      if(src != null){
         ret.setUsuaCodi(infinitusAPIUsername);
         ret.setUsuario(infinitusAPIUsername);
         ret.setPassword(infinitusAPIPassword);
         ret.setRelaCuentaNume(src.getOrdenante().getCuenta());
         ret.setRelaCuentaNumeDestino(src.getBeneficiario().getCuenta());
         ret.setPagaOtorEntiCodi(src.getOrdenante().getEmisor());
         ret.setPagaOtorEntiCodiDestino(src.getBeneficiario().getEmisor());
         ret.setAutoImpor(src.getMonto().doubleValue()); // use only BigDecimal
         ret.setMoneCodi(sistarMappingUtils.getCurrencyIdFromISO(src.getMoneda()));
         ret.setObservaciones(src.getReferencia());
         ret.setMarcaCodi(src.getMarca());
         ret.setRequestExternalId(src.getRrn());
         switch(ret.getMoneCodi()){
            case 1:
               ret.setPlanCodi("44");
               break;
            case 2:
               ret.setPlanCodi("45");
               break;
         }
         return ret;
      }
      return null;
   };

   private final Converter<ReverseRequestDto, RevertirTransferenciaC2C2> c2cRevertTransferConverter = ctx -> {
      RevertirTransferenciaC2C2 ret = new RevertirTransferenciaC2C2();
      ReverseRequestDto src = ctx.getSource();
      if(src != null){
         ret.setUsuaCodi(infinitusAPIUsername);
         ret.setPassword(infinitusAPIPassword);
         ret.setPagaOtorEntiCodi(src.getEmisor());
         ret.setAjusConcepCodiRever(280);
         ret.setAjusConcepCodiReverExt(281);
         ret.setRequestExternalId(src.getRrn());
         ret.setRequestExternalIdARevertir(src.getToReverseRrn());
         return ret;
      }
      return null;
   };

   private final Converter<ReemplazoRequestDto, ReemplazoTarjetaPrepagaInnominadaXCi2> replacementByIDConverter = ctx -> {
      ReemplazoRequestDto src = ctx.getSource();
      ReemplazoTarjetaPrepagaInnominadaXCi2 dest = new ReemplazoTarjetaPrepagaInnominadaXCi2();
      dest.setUsuaCodi(infinitusAPIUsername);
      dest.setPassword(infinitusAPIPassword);
      dest.setTipoDocuCodi(Integer.parseInt(src.getTipoDocumento()));
      dest.setDocuNume(src.getDocumento());
      dest.setNumSobre(src.getNroSobre());
      dest.setPagaOtorEntiCodi(src.getEmisor());

      String paddedCode = StringUtils.leftPad(src.getPais(), 3, "0");
      String paisDoc = config.getDoc_type_without_country().contains(src.getTipoDocumento()) ? "000" : paddedCode;
      dest.setPaidDocCustomer(paisDoc);

      return dest;
   };

   private final Converter<ReemplazoRequestDto, ReemplazoTarjetaPrepagaInnominada2> replacementByAccountConverter = ctx -> {
      ReemplazoRequestDto src = ctx.getSource();
      ReemplazoTarjetaPrepagaInnominada2 dest = new ReemplazoTarjetaPrepagaInnominada2();
      dest.setUsuaCodi(infinitusAPIUsername);
      dest.setPassword(infinitusAPIPassword);
      dest.setTipoDocuCodi(Integer.valueOf(src.getTipoDocumento()));
      dest.setPersoDocuNume(src.getDocumento());
      dest.setNumSobre(src.getNroSobre());
      dest.setAdiNume(0);
      dest.setSoliNume(src.getCuenta());
      return dest;
   };

   private final Converter<String, Integer> paisConverter = ctx -> {
      if (Objects.isNull(ctx.getSource())) {
         return 71;
      }
      return sistarMappingUtils.getCodPais(ctx.getSource());
   };

   private final Converter<DireccionDto, String> departamentoConverter = ctx -> {
      if (Objects.isNull(ctx.getSource())) {
         return "";
      }
      DireccionDto dir = ctx.getSource();
      return externalData.getCodDepartamento(dir.getDepartamento(), dir.getPais());
   };

   private final Converter<DireccionDto, Integer> departamentoIntegerConverter = ctx -> {
      if (Objects.isNull(ctx.getSource())) {
         return null;
      }
      DireccionDto dir = ctx.getSource();
      String code = externalData.getCodDepartamento(dir.getDepartamento(), dir.getPais());
      return code == null ? null: Integer.parseInt(code);
   };

   private final Converter<DireccionDto, String> localidadConverter = ctx -> localidadMapping(ctx.getSource());

   private final Converter<DireccionDto, String> localidadStringConverter = ctx -> localidadMapping(ctx.getSource());

   private String localidadMapping(DireccionDto address) {
      if (Objects.isNull(address)) {
         return "0";
      }
      return externalData.getCodLocalidad(address.getLocalidad(), address.getDepartamento(), address.getPais());
   }

   private final Converter<String, String> paisDescConverter = ctx -> {
      if (Objects.isNull(ctx.getSource())) {
         return "";
      }
      return sistarMappingUtils.getDescPais(ctx.getSource());
   };

   private final Converter<String, String> trimStringConverter = ctx -> Objects.isNull(ctx.getSource()) ? null : ctx.getSource().trim();
   
   private final Converter<Long, String> brandNameConverter = ctx -> {
      if(Objects.isNull(ctx.getSource())){
         return null;
      }
      String issuer = String.valueOf(ctx.getSource());
     return sistarMappingUtils.getBrandNameFromIssuerId(issuer);
   };

   private final Converter<TarjetDTO, CodeNameTuple> gafConverter2 = ctx -> {
      if(Objects.isNull(ctx.getSource())){
         return null;
      }
      CodeNameTuple ret = new CodeNameTuple();
      TarjetDTO card = ctx.getSource();
      ret.setCodigo(null);
      ret.setNombre(card.getAfinityGroupName());

      return ret;
   };

   private final Converter<TarjetaCIBanco, CodeNameTuple> gafConverter = ctx -> {
      if(Objects.isNull(ctx.getSource())){
         return null;
      }
      CodeNameTuple ret = new CodeNameTuple();
      TarjetaCIBanco card = ctx.getSource();
      String sistarGAFCode = card.getCodigo_Grupo_Afinidad() != null ? String.valueOf(card.getCodigo_Grupo_Afinidad()): null;
      
      if(sistarGAFCode != null){
         ret.setCodigo(sistarGAFCode.length() > 2 ? Long.parseLong(sistarGAFCode.substring(2)): Long.parseLong(sistarGAFCode));
         ret.setNombre(card.getDescripcion_Grupo_Afinidad());
      }

      return ret;
   };
   
   private final Converter<String, Byte> statusToBlockingCodeConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      return sistarMappingUtils.getBlockingCode(ctx.getSource());
   };

   private final Converter<MOVENPERIODOITEM, String> transactionStateConverter = ctx -> {
      if(Objects.isNull(ctx.getSource())){
         return null;
      }
      MOVENPERIODOITEM mov = ctx.getSource();
      return String.format("%02d-%02d", mov.getESTADOTIPO(), mov.getSUBCODTX());
   };

   private final Converter<MOVENPERIODOITEM, String> transactionCodeConverter = ctx -> {
      if(Objects.isNull(ctx.getSource())){
         return null;
      }
      
      MOVENPERIODOITEM mov = ctx.getSource();
      Integer mti = Integer.parseInt(mov.getMTI());
      Integer pCode = Integer.parseInt(mov.getPROCESSINGCODE());

      return String.format("%s.%02d", mti, pCode);
   };

   private final Converter<MOVENPERIODOITEM, String> transactionDescConverter = ctx -> {
      if(Objects.isNull(ctx.getSource())){
         return null;
      }
      MOVENPERIODOITEM mov = ctx.getSource();
      return String.format("%s-%s", mov.getCODTX(), mov.getSUBCODTX());
   };

   private final Converter<String, String> multipleToSingleSpaceConverter = ctx -> {
      if(Objects.isNull(ctx.getSource())){
         return null;
      }
      String multiSpaceStr = ctx.getSource();
      return multiSpaceStr.replaceAll("\\s{2,}", " ");
   };

   private final Converter<MOVENPERIODOITEM, Boolean> isConfirmedConverter = ctx -> {
      if (Objects.isNull(ctx.getSource())) {
         return false;
      }
      MOVENPERIODOITEM mov = ctx.getSource();
      //TODO: verificar correcta evaluacion del estado
      return mov.getRESPONSECODE().equals("00");
   };


   private final Converter<MOVENPERIODOITEM, String> movementTypeToITCConverter = ctx -> {
      if(Objects.isNull(ctx.getSource())){
         return null;
      }
      MOVENPERIODOITEM mov = ctx.getSource();
      // String itc = mov.getMTI().substring(1) + "." + mov.getPROCESSINGCODE().substring(mov.getPROCESSINGCODE().length() - 2);
      return sistarMappingUtils.getITCFromSistarMovement(mov);
   };

    private final Converter<MOVENPERIODOITEM, String> movementFieldsToRrnConverter = ctx -> {
        if (Objects.isNull(ctx.getSource())) {
            return null;
        }
        MOVENPERIODOITEM mov = ctx.getSource();
        String fieldsRrn = (mov.getACCOUNTNUMBER() + mov.getTRANSACTIONBIN() + mov.getTRANSACTIONDATE() + mov.getORIGINALCURRENCY() +
                mov.getORIGINALAMOUNT() + mov.getAUTHORIZATIONCODE());
        byte[] bytesRrn = fieldsRrn.getBytes();
        Checksum checksum = new CRC32();
        checksum.update(bytesRrn,0,bytesRrn.length);
        long checksumValue = checksum.getValue();
        return String.valueOf(checksumValue);
    };

   private final Converter<Integer, Short> currencyISOtoIdConverter = ctx -> ctx.getSource() == null
         ? null
         : sistarMappingUtils.getCurrencyIdFromISO(ctx.getSource());

   private final Converter<Integer, Short> currencyISOtoIdConverter2 = ctx -> {
     if(ctx.getSource() == null){
       return null;
     }
     Short currencyId = sistarMappingUtils.getCurrencyIdFromISO(ctx.getSource());
     return currencyId != null ? currencyId: ctx.getSource().shortValue();
   };

   private final Converter<String, String> lastFourDigitsConverter = ctx -> {
      String cardnumber = ctx.getSource();
      if (Objects.isNull(cardnumber)) {
         return null;
      }
      return cardnumber.substring(cardnumber.length() - 4);
   };

   private final Converter<String, Date> lastDayOfMonthConverter = ctx -> {
      if(ctx.getSource() != null){
         String inputStrDate = ctx.getSource();
         String expireYYYYMMDD = "";
         if(inputStrDate.matches("^\\d{6}$")){ // assume yyyyMM format
            expireYYYYMMDD = inputStrDate + "01";
         }else if(inputStrDate.matches("^\\d{4}$")){ // assume yyMM format
            expireYYYYMMDD = "20" + inputStrDate + "01";
         }
         LocalDate lastDayOfMonth = LocalDate.parse(expireYYYYMMDD, DateTimeFormatter.ofPattern("yyyyMMdd"))
            .with(TemporalAdjusters.lastDayOfMonth());
         return java.sql.Date.valueOf(lastDayOfMonth);
      }
      return null;
   };

   private final Converter<Boolean, String> boolConverter = ctx -> ctx.getSource() != null && ctx.getSource() ? "S" : "N";

   private final Converter<List<TelefonoDto>, String> landlinePhoneConverter = ctx -> {
      for (TelefonoDto item : ctx.getSource()) {
         if (item.getTipoTel() == 2 || item.getTipoTel() == 4) {
            return item.getNum();
         }
      }
      return null;
   };

   private final Converter<List<TelefonoDto>, String> mobilePhoneConverter = ctx -> {
      for (TelefonoDto item : ctx.getSource()) {
         if (item.getTipoTel() == 3 || item.getTipoTel() == 1) {
            return item.getNum();
         }
      }
      return null;
   };

   private final Converter<NewCardRequestDto, SistarbancMDF2ABMCUENTATARJETA> remainingFieldsConverter = ctx -> {
      NewCardRequestDto source = ctx.getSource();
      SistarbancMDF2ABMCUENTATARJETA destination = ctx.getDestination();
      AccountDTO account = destination.getAccount();
      BeneficiarioDto adultBeneficiaryOrRepresentative = ObjectUtils.defaultIfNull(source.getRepresentante(), source.getBeneficiario());
      if (Objects.nonNull(account.getPrincipalCustomer())) {
         account.getPrincipalCustomer().getCustomerDTO().forEach(e -> {
            e.getProduct().setProductCode(NumberUtils.toByte(source.getProducto()));
            e.getProduct().setCreditLimitPorce(CREDIT_LIMIT_PORC);
            e.getProduct().setAfinityGroupCode(NumberUtils.toShort(source.getGrupoAfinidad()));
         });
         account.setBrandCode(source.getMarca().toString());
         account.setCtaBcoSuc(source.getSucursal());
         account.setCtaBcoSuc2(source.getSucursalEntrega());
         account.setActId(convertActivityStatus(adultBeneficiaryOrRepresentative.getActividad()));
         account.setCodActId(convertActivityCode(adultBeneficiaryOrRepresentative.getActividad()));
         account.setBcoId(source.getEmisor());
         account.setOperaPorTerceros(BooleanUtils.toString(source.getOperaPorTerceros(), "S", "N"));
         account.setCtaTpo(CTA_TIPO_PERSONAL);
         account.setCtaCicId(config.getCtacicid_default_value());
         account.setCtaCraId(CARTERA_CUENTA_STANDARD);
         account.setPaiid(adultBeneficiaryOrRepresentative.getNacionalidad());
         account.setEnvId(CE_NO_IMPRIME);
         account.setDocumentNumberT(adultBeneficiaryOrRepresentative.getDoc().getNum());
         account.setDocumentTypeT(sistarMappingUtils.getDocType(adultBeneficiaryOrRepresentative.getDoc().getTipoDoc()));
      }
      return destination;
   };

   private final Converter<BeneficiarioDto, String> embossingNameConverter = ctx -> {
      if (Objects.isNull(ctx.getSource())) {
         return null;
      }
      BeneficiarioDto beneficiary = ctx.getSource();
      if (beneficiary == null || beneficiary.getApellido() == null || beneficiary.getNombre() == null) {
         return null;
      }
      return StringUtils.stripAccents(String.format("%s %s", beneficiary.getApellido().trim().toUpperCase(), beneficiary.getNombre().trim().toUpperCase()));
   };

   private final Converter<String, String> genderConverter = ctx -> sistarMappingUtils.getGender(ctx.getSource());

   private final Converter<String, String> docTypeConverter = ctx -> sistarMappingUtils.getDocType(ctx.getSource());
   
   private final Converter<String, String> docTypeNameConverter = ctx -> sistarMappingUtils.getDocTypeByName(ctx.getSource());

   private final Converter<TarjetaCIBanco, Card.Status> cardStatusConverter = ctx -> {
     if(ctx.getSource() == null){
       return null;
     }
     TarjetaCIBanco card = ctx.getSource();
     Card.Status status = sistarMappingUtils.getCardStatus(card.getEstado_Tarjeta(), card.getCodigo_de_Bloqueo());

     // Si la tarjeta no tiene ningun bloqueo inpuesto por el sistema/backoffice, devuelvo el estado desde el punto de vista del bloqueo impuesto por el usuario
     // en caso contrario devuelvo el estado segun el tipo de bloqueo
     if(status == Card.Status.ACTIVE){
       return sistarMappingUtils.getCardStatus(card.getTarjeta_Inhabilitada());
     }
     return status;
   };

   private final Converter<String, Card.Status> cardStatusConverter2 = ctx -> {
     if(ctx.getSource() == null){
       return null;
     }
    return sistarMappingUtils.getCardStatus(ctx.getSource());
   };

   private final Converter<DireccionDto, String> addressConverter = ctx -> {
      if (Objects.isNull(ctx.getSource())) {
         return null;
      }
      DireccionDto address = ctx.getSource();
      String addressStr = address.getCalle() + //
            (StringUtils.isEmpty(address.getPuerta()) ? "" : " " + address.getPuerta()) + //
            (StringUtils.isEmpty(address.getApto()) ? "" : " ap. " + address.getApto()) + //
            (StringUtils.isEmpty(address.getPiso()) ? "" : " piso " + address.getPiso());
      return StringUtils.stripAccents(addressStr);
   };

   private final Converter<CreditRequestDto, String> subCodMovimientoConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      CreditRequestDto source = ctx.getSource();
      String code = source.getCanal() != null ? sistarMappingUtils.getSubCodeByChannel(source.getCanal().toLowerCase()): null;
      if(code == null){
         code = sistarMappingUtils.getSubCodeByProduct(source.getProducto(), source.getRed());
      }
      return code;
   };
   
   private final Converter<ReverseRequestDto, String> subCodMovimientoConverter3 = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      ReverseRequestDto source = ctx.getSource();
      Boolean debit = !source.getReversaRecarga();
     return source.getCanal() != null ? sistarMappingUtils.getSubCodeByChannel(source.getCanal().toLowerCase() + (debit ? "-debit": "")): null;
   };
   
   private final Converter<CreditRequestDto, Integer> codMovimientoConverter = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      CreditRequestDto source = ctx.getSource();
      Integer code = source.getCanal() != null ? sistarMappingUtils.getCodeByChannel(source.getCanal().toLowerCase()): null;
      if(code == null){
          code = NumberUtils.toInt(sistarMappingUtils.getCodeByProduct(source.getProducto(), source.getRed()), 0);      }
      return code;
   };

   private final Converter<ReverseRequestDto, Integer> codMovimientoConverter3 = ctx -> {
      if(ctx.getSource() == null){
         return null;
      }
      ReverseRequestDto source = ctx.getSource();
      Boolean debit = !source.getReversaRecarga();
     return source.getCanal() != null ? sistarMappingUtils.getCodeByChannel(source.getCanal().toLowerCase() + (debit ? "-debit": "")): null;
   };

   private String convertActivityCode(String source) {
      if (StringUtils.isEmpty(source) || source.length() < 3) {
         return null;
      }
      return source.substring(source.length() - 3);
   }

   private String convertActivityStatus(String source) {
      if (StringUtils.isEmpty(source)) {
         return StringUtils.EMPTY;
      }
      switch (source.substring(0, 1)) {
         case "8":
            return "I";
         case "4":
            return "R";
         default:
            return "A";
      }
   }

}
