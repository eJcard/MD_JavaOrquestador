package uy.com.md.sistar.Schemas.json.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import uy.com.md.sistar.util.SistarConstants;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DireccionDto {

   @Size(min = 1, max = 40)
   @Schema(required = true)
   private String calle;
   
   @Size(min = 1, max = 40)
   private String amplicacionCalle;
   
   @Schema(required = true)
   @Size(min = 1, max = 5)
   private String puerta;
   
   
   @Size(min = 1, max = 5)
   private String apto;

   @Min(1)
   @Max(999)
   private Integer departamento;
   
   @Min(1)
   @Max(999)
   private Integer localidad;   
   
   @Min(1)
   @Max(100)
   private String piso;
   
   @Schema(required = true)
   private Boolean residente;
   
   @Size(min = 3, max = 10)
   @Pattern(regexp = SistarConstants.ZIP_CODE_PATTERN)
   private String codigoPostal;
   
   @Pattern(regexp = SistarConstants.COUNTRY_CODE_PATTERN)
   @Schema(example = "858", description = SistarConstants.COUNTRY_CODE_DESCRIPTION, required = true)
   private String pais;
   
   @Min(1)
   @Max(999)
   private Integer barrio;
}
