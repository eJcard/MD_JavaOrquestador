package uy.com.md.sistar.dto.in;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import uy.com.md.sistar.dto.IdentificationDto;
import uy.com.md.sistar.util.SistarConstants;

@Data
public class CreditRequestDto {
	@Schema(allowableValues = {"terceros", "creditos"}, required = true)
   private String canal;
   
   @Pattern(regexp = SistarConstants.NUMBERX3_CODE_PATTERN)
   @Schema(required = true)
   private String red;
   
   @Pattern(regexp = SistarConstants.NUMBERX3_CODE_PATTERN)
   @Schema(required = true)
   private String sucursal;
   
   @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
   @Schema(required = true)
   private String cuenta;
   
   @Schema(required = true)
   private Double monto;
   
   @Schema(required = true, description = SistarConstants.COUNTRY_CODE_DESCRIPTION, example = "858")
   @Pattern(regexp = SistarConstants.COUNTRY_CODE_PATTERN)
   private Integer moneda;

   private IdentificationDto beneficiario;

   @Size(min = 1, max = 15)
   @Pattern(regexp = SistarConstants.INVOICE_NUMBER_PATTERN)
   private String factura;
   
   @Schema(
      allowableValues = {
         "alquileres",
         "adelantos",
         "sueldos",
         "proveedores",
         "honorariosProf",
         "serviciosPersonales",
         "prestacionesSociales",
         "jubilacionesPensiones",
         "otros"
      }
   )
   private String motivo;

   @Size(min = 1, max = 15)
   @Pattern(regexp = SistarConstants.INVOICE_NUMBER_PATTERN)
   private String comprobante;
   
   private String referencia;
   
   @Schema(required = true)
	@Size(min = 1, max = 8)
	@Pattern(regexp = SistarConstants.RRN_PATTERN)
   private String rrn;

   @Schema(required = true)
   private String producto;

   @Schema(required = true)
   private Long marca;

   @Schema(required = true)
   private Integer emisor;
}
