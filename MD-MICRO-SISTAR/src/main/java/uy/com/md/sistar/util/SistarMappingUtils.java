package uy.com.md.sistar.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.TimeZones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;
import uy.com.md.common.movimientos.TransaccionesDto;
import uy.com.md.sistar.dto.out.ProductInfo;
import uy.com.md.sistar.service.SistarServiceProperties;
import uy.com.md.sistar.service.SistarServiceProperties.Currency;
import uy.com.md.sistar.service.SistarServiceProperties.Product;
import uy.com.md.sistar.service.SistarServiceProperties.SBTrnCodes;
import uy.com.md.sistar.soap.client.MOVENPERIODOITEM;


@Component
public class SistarMappingUtils {

   final Logger logger = LoggerFactory.getLogger(SistarMappingUtils.class);

   @Autowired
   SistarServiceProperties config;

   public SistarServiceProperties getConfig() {
      return config;
   }

   public String getCurrencySymbol(String isoCode){
      Currency currency = config.getCurrencies().get("id-" + isoCode);
      if(currency != null){
         return currency.getSymbol();
      }
      return null;
   }

   public Short getCurrencyIdFromISO(int isoCode) {
      switch (isoCode) {
         case 858:
            return (short) 1;
         case 840:
            return (short) 2;
         default:
            return null;
      }
   }

   public String getGender(String mdGender) {
      if (StringUtils.isNotBlank(mdGender)) {
         switch (mdGender.toUpperCase()) {
            case "MAS":
            case "M":
               return "M";
            case "FEM":
            case "F":
               return "F";
         }
      }
      return "O";
   }

   public String getDocTypeByName(String name) {
      return DocType.getTypeByName(name);
   }

   public String getDocType(String mdDocType) {
      return DocType.getType(mdDocType);
   }

   public String getBrandCode(String brandName) {
      if (StringUtils.isNotBlank(brandName)) {
         switch (brandName.toLowerCase()) {
            case "mastercard":
               return "3";
            case "visa":
               return "1";
         }
      }
      return "1";
   }
   
   public String getITCFromSistarMovement(MOVENPERIODOITEM mov){
      if(mov == null){
         return null;
      }
      String code = String.valueOf(mov.getCODTX());
      String subcode = String.valueOf(mov.getSUBCODTX());
      String estadoTipo = String.valueOf(mov.getESTADOTIPO());
      String estadoCodi = String.valueOf(mov.getESTADOCODI());
      boolean isNegative = mov.getORIGINALAMOUNT() < 0 || mov.getCARDHOLDERAMOUNT() < 0;

      String itc = "";
      if(isNegative){
         itc = config.getItc_by_sbcode().getOrDefault(String.format("sbcode-%s-%s-%s-%s-N", code, subcode, estadoTipo, estadoCodi), "");
      }
      if(itc.isEmpty()){
         itc = config.getItc_by_sbcode().getOrDefault(String.format("sbcode-%s-%s-%s-%s", code, subcode, estadoTipo, estadoCodi), "");
      }
      if(itc.isEmpty() && isNegative){
         itc = config.getItc_by_sbcode().getOrDefault(String.format("sbcode-%s-%s-N", code, subcode), "");
      }
      if(itc.isEmpty()){
         itc = config.getItc_by_sbcode().getOrDefault(String.format("sbcode-%s-%s", code, subcode), "");
      }
      if(itc.isEmpty()){
         itc = config.getItc_by_sbcode().getOrDefault("sbcode-" + code + "-default", "");
      }
      if(itc.isEmpty()){
         logger.warn("ITC not found for code: {}, subcode: {}, account: {}", code, subcode, mov.getACCOUNTNUMBER());
      }
      
      if(itc.isEmpty() &&
         (config.getAlternative_itc_mapping_enabled().equals("force") ||
         (code.equals("0") && subcode.equals("0") && config.getAlternative_itc_mapping_enabled().equals("true")))){
         switch (estadoTipo) {
            case "6":
            case "8":
               if(mov.getCARDHOLDERAMOUNT() > 0){
                  itc = "200.01";
               }else{
                  itc = "100.22";
               }
               break;
            case "12":
               if(mov.getCARDHOLDERAMOUNT() > 0){
                  itc = "F200.22";
               }else{
                  itc = "F200.02";
               }
               break;
            case "14":
               itc = "200.21";
               break;
         }
      }

      return itc;
   }

   public int getCodPais(String codigo) {
      switch (codigo) {
         case "858":
            return 1;
         case "032":
            return 2;
         case "076":
            return 3;
         default:
            return 0;
      }
   }

   public XMLGregorianCalendar dateFromYyyymmdd(String origDate) {
      try {
         GregorianCalendar cal = new GregorianCalendar();
         DateTimeFormatter format = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyyMMdd"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .toFormatter();
         LocalDate localDate = LocalDate.parse(origDate, format);
         
         cal.setTimeZone(TimeZone.getTimeZone(TimeZones.GMT_ID));
         cal.set(Calendar.HOUR, 0);
         cal.set(Calendar.MINUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         cal.set(Calendar.YEAR, localDate.getYear());
         cal.set(Calendar.MONTH, localDate.getMonthValue() - 1);
         cal.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());

         return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
      } catch (DatatypeConfigurationException e) {
         return null;
      }
   }

   public String getDescPais(String codigo) {
      switch (codigo) {
         case "858":
            return "URUGUAY";
         case "032":
            return "ARGENTINA";
         case "076":
            return "BRASIL";
         default:
            return "OTRO";
      }
   }

   public ProductInfo.Card.Status getCardStatus(String statusCode) {
      switch (statusCode){
         case "A":
            return ProductInfo.Card.Status.ACTIVE;
         case "D":
            return ProductInfo.Card.Status.SUSPENDED_ON_REQUEST;
         default:
            return null;
      }
   }

   public ProductInfo.Card.Status getCardStatus(String statusType, String code) {
      switch(code){
         case "0":
            return ProductInfo.Card.Status.ACTIVE;
         case "98":
            return config.isForce_block_status() ? ProductInfo.Card.Status.SUSPENDED_ON_REQUEST: ProductInfo.Card.Status.SUSPENDED_ACTIVE;
         case "95":
            return ProductInfo.Card.Status.FRAUD;
         case "99":
            return ProductInfo.Card.Status.LOST;
         default:
            return ProductInfo.Card.Status.UNKNOWN;
      }
   }

   @SneakyThrows
   public Byte getBlockingCode(String source) {
      switch (StringUtils.defaultIfBlank(source, "").toUpperCase()) {
         case "ACTIVE":
            return 0;
         case "SUSPENDED":
         case "SUSPENDED_ON_REQUEST":
            return 98;
         case "FRAUD":
            return 95;
         case "STOLEN":
         case "LOST":
            return 99;
         default:
            throw new IllegalArgumentException("Codigo de estado no válido. Los valores admitidos son: active, suspended y suspended_on_request.");
      }
   }

   public String getBrandNameFromIssuerId(String issuerId){
      return Optional.ofNullable(config)
         .map(SistarServiceProperties::getIssuers_by_id)
         .map(issuers -> issuers.get(String.format("id-%s", issuerId)))
         .map(SistarServiceProperties.Issuer::getName)
         .orElse(null);
   }

  public String getCurrencyISOFromCurrencyId(String originalCode) {
     switch (originalCode) {
        case "1":
           return "858";
        case "2":
           return "840";
        default:
           return null;
     }
  }

   public String getSubCodeByProduct(String productId, String idRed){
      Product info = getProductInfo(productId, idRed);
      return info != null ? info.getSubcode(): null;
   }

   private Product getProductInfo(String productId, String idRed) {
      List<String> searchKeys = Arrays.asList(
              "id-" + productId + "-net-" + idRed,
              "id-" + productId,
              "default"
      );
      Map<String, Product> products = config.getProducts();
      return searchKeys.stream()
              .filter(products::containsKey)
              .findFirst()
              .map(products::get)
              .orElse(null);
   }
   public String getSubCodeByChannel(String channel){
      SBTrnCodes codes = config.getChannels().get(channel);
      if(codes != null){
         return codes.getSubcode();
      }else{
         return Optional
            .of(config.getChannels())
            .map(m -> m.get("default"))
            .map(SBTrnCodes::getSubcode)
            .orElse(null);
      }
   }

      public String getCodeByProduct(String productId, String idRed) {
         Product info = getProductInfo(productId, idRed);
         return info != null ? info.getCode() : null;
      }

   public Integer getCodeByChannel(String channel){
      SBTrnCodes codes = config.getChannels().get(channel);
      if(codes != null){
         return Integer.valueOf(codes.getCode());
      }else{
         return Optional
         .of(config.getChannels())
         .map(m -> m.get("default"))
         .map(SBTrnCodes::getCode)
         .map(Integer::valueOf)
         .orElse(null);
      }
   }

   public String getMovementDescription(TransaccionesDto m, String defaultValue) {
      String description = config.getMovement_description().getOrDefault(String.format("cod-%s-scod-%s", m.getSbCode(), m.getSbSubcode()), "");

      if(config.getEnable_feature_umscr603() && m.getSbCode() == 25 && m.getSbSubcode() == 9 && !m.getObservaciones().isEmpty())
         description = m.getObservaciones();

      description = description.isEmpty() ? config.getMovement_description().getOrDefault(String.format("cod-%s-scod-default", m.getSbCode()), ""): description;
      description = description.isEmpty() ? config.getMovement_description().getOrDefault(String.format("itc-%s", m.getCodigo().replaceAll("\\.", "_")), defaultValue) : description;

      if(description != null){
         String currencyDesc = config.getCurrencies().getOrDefault("id-" + m.getMoneda(), new Currency("", "", "")).getDesc();
         description = description.replaceAll("\\{currency\\}", currencyDesc != null ? currencyDesc: "");
      }

      return description != null ? description.toUpperCase(): null;
   }

   public String getMovementMerchantDescription(TransaccionesDto m, String defaultValue) {
      String description = config.getMovement_merchant().getOrDefault(String.format("cod-%s-scod-%s", m.getSbCode(), m.getSbSubcode()), "");
      description = description.isEmpty() ? config.getMovement_merchant().getOrDefault(String.format("cod-%s-scod-default", m.getSbCode()), ""): description;
      description = description.isEmpty() ? config.getMovement_merchant().getOrDefault(String.format("itc-%s", m.getCodigo().replaceAll("\\.", "_")), defaultValue) : description;

      if(description != null){
         String currencyDesc = config.getCurrencies().getOrDefault("id-" + m.getMoneda(), new Currency("", "", "")).getDesc();
         description = description.replaceAll("\\{currency\\}", currencyDesc != null ? currencyDesc: "");
      }
      return description != null ? description.toUpperCase(): null;
   }

   public boolean shouldExclude(TransaccionesDto t){
      String code = String.valueOf(t.getSbCode());
      String scode = String.valueOf(t.getSbSubcode());
      String statusCode = String.valueOf(t.getSbStatusCode());
      String statusType = String.valueOf(t.getSbStatusType());
      Set<String> s = config.getExclude_movements();
      String escapedITC = t.getCodigo().replaceAll("\\.", "_");
      boolean isNegative = t.getMonto().compareTo(BigDecimal.ZERO) < 0;

      return s.contains(String.format("cod-%s-scod-%s-type-%s-stat-%s", code, scode, statusType, statusCode)) ||
          s.contains(String.format("cod-%s-scod-%s", code, scode)) ||
          (isNegative && s.contains(String.format("cod-%s-scod-%s-N", code, scode))) ||
          s.contains(String.format("cod-%s-scod-default", code)) ||
          (isNegative && s.contains(String.format("cod-%s-scod-default-N", code))) ||
          s.contains(String.format("stat-%s-type-%s", statusCode, statusType)) ||
          (isNegative && s.contains(String.format("stat-%s-type-%s-N", statusCode, statusType))) ||
          s.contains(String.format("itc-%s", escapedITC)) ||
          s.contains(String.format("itc-%s-N", escapedITC));
   }

  public Integer getBranchOffice(String canal, String producto) {
     return config.getForce_branch_office().get(String.format("ch-%s", canal));
  }
   public String getMccByCodSubcode(TransaccionesDto m, String mcc) {
      String mccN = config.getMcc_code_sbcode().getOrDefault(String.format("cod-%s-scod-%s", m.getSbCode(), m.getSbSubcode()), mcc);
      return mccN;
   }
}
