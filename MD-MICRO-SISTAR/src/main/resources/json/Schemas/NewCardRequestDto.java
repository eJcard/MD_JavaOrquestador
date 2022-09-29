package uy.com.md.sistar.Schemas.json.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import uy.com.md.sistar.Schemas.json.out.BeneficiarioDto;
import uy.com.md.sistar.util.SistarConstants;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewCardRequestDto {

   private BeneficiarioDto beneficiario;

   @Size(min = 1, max = 3)
   private String sucursal;

   @Size(min = 1, max = 3)
   private String sucursalEntrega;

   @Size(min = 1, max = 3)
   private String sucursalSolicitud;

   @Size(min = 1, max = 3)
   @Pattern(regexp = SistarConstants.NUMBERX3_CODE_PATTERN)
   @Schema(required = true)
   private String producto;

   @Size(min = 1, max = 3)
   @Pattern(regexp = SistarConstants.NUMBERX2_CODE_PATTERN)
   @Schema(required = true)
   private String red;

   @Size(min = 1, max = 3)
   @Pattern(regexp = SistarConstants.NUMBERX2_CODE_PATTERN)
   @Schema(required = true)
   private String redSolicitud;

   @Size(min = 1, max = 3)
   @Pattern(regexp = SistarConstants.NUMBERX3_CODE_PATTERN)
   @Schema(required = true)
   private String grupoAfinidad;

   @Schema(allowableValues = { "1", "3" }, required = true)
   private Long marca = 1L;

   private BeneficiarioDto representante;

   @Size(min = 1, max = 3)
   @Pattern(regexp = SistarConstants.NUMBERX3_CODE_PATTERN)
   @Schema(required = true)
   private String emisor;

   private Boolean operaPorTerceros = false;

   @Size(min = 1, max = 18)
   @Pattern(regexp = SistarConstants.TXNID_PATTERN)
   @Schema(example = "12345678")
   private Long rrn;

   private String numeroDeSobre;
}
