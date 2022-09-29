package uy.com.md.sistar.Schemas.json.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import uy.com.md.sistar.util.SistarConstants;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

@Data
@JsonInclude(Include.NON_NULL)
public class TransactionInfo {

   /**
    * AdditionalInfo
    */
   @Data
   @JsonInclude(Include.NON_NULL)
   public static class AdditionalData {

      /**
       * CustomerInfo
       */
      @Data
      public static class CustomerInfo {

         @JsonProperty("tipDoc")
         @JsonInclude(Include.NON_NULL)
         @Schema(example = "2", required = true)
         @Pattern(regexp = SistarConstants.NUMBERX2_CODE_PATTERN) String documentType;

         @JsonProperty("doc")
         @JsonInclude(Include.NON_NULL)
         @Schema(example = SistarConstants.ID_EXAMPLE, required = true)
         @Pattern(regexp = SistarConstants.ID_PATTERN) String documentNumber;

         @JsonProperty("cuenta")
         @JsonInclude(Include.NON_NULL)
         @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
         @Schema(example = SistarConstants.ACCOUNT_NUMBER_EXAMPLE)
         String account;
      }

      /**
       * BankAccountInfo
       */
      @Data
      @JsonInclude(Include.NON_NULL)
      public static class BankAccountInfo {

         @JsonProperty("cuenta")
         @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
         @Schema(example = SistarConstants.ACCOUNT_NUMBER_EXAMPLE)
         String account;

         @JsonProperty("nombre")
         String bankName;

         @JsonProperty("beneficiario")
         String beneficiaryName;
      }

      @JsonProperty("ordenante")
      CustomerInfo requester;

      @JsonProperty("banco")
      BankAccountInfo bank;

      @JsonProperty("motivo")
      @Schema(allowableValues = { "alquileres", "adelantos", "sueldos", "proveedores", "honorariosProf", "serviciosPersonales",
            "prestacionesSociales", "jubilacionesPensiones", "otros" })
      String reason;

      @JsonProperty("comprobante")
      String voucher;

      @JsonProperty("id")
      String id;

      @JsonProperty("reversado")
      boolean reversed;
   }

   @JsonProperty("date")
   @Schema(pattern = SistarConstants.DATE_FORMAT, example = "2020-12-01")
   Date date;

   @JsonProperty("descripcion")
   @Schema(example = "Compra")
   String description;

   @JsonProperty("codigo")
   @Pattern(regexp = "\\d{1,2}")
   @Schema(example = "4", description = "Transaction type code")
   String code;

   @JsonProperty("estado")
   String status;

   @JsonProperty("codigoAprobacion")
   @Size(min = 1, max = 6) @Pattern(regexp = "^[0-9a-zA-Z]{1,6}$")
   @Schema(example = "123456")
   String approvalCode;

   @JsonProperty("comercio")
   @Size(min = 1, max = 3) @Pattern(regexp = SistarConstants.NUMBERX3_CODE_PATTERN) String merchantId;

   @JsonProperty("comercioDescripcion")
   @JsonInclude(Include.NON_NULL)
   String merchantDescription;

   @JsonProperty("moneda")
   @Pattern(regexp = SistarConstants.COUNTRY_CODE_PATTERN)
   @Schema(example = SistarConstants.COUNTRY_CODE_DESCRIPTION)
   String currency;

   @JsonProperty("monedaOrigen")
   @Pattern(regexp = SistarConstants.COUNTRY_CODE_PATTERN)
   @Schema(example = SistarConstants.COUNTRY_CODE_DESCRIPTION)
   String originalCurrency;

   @JsonProperty("monto")

   BigDecimal amount;

   @JsonProperty("montoOrigen")
   BigDecimal originalAmount;

   @JsonProperty("confirmado")
   Boolean confirmed;

   @JsonProperty("irc")
   @Pattern(regexp = "\\d{4}")
   @Schema(example = "0000", description = "Status code")
   String irc;

   @JsonProperty("mcc")
   @Pattern(regexp = SistarConstants.MCC_PATTERN)
   @Schema(example = "7123")
   String mcc;

   @JsonProperty("datosAdicionales")
   AdditionalData additionalData;
}
