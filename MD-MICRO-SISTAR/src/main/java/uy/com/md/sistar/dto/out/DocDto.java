package uy.com.md.sistar.dto.out;

import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import uy.com.md.sistar.util.SistarConstants;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocDto {

   @Pattern(regexp = SistarConstants.COUNTRY_CODE_PATTERN)
   @Schema(example = "858", description = SistarConstants.COUNTRY_CODE_DESCRIPTION, required = true)
   private String pais;

   @Pattern(regexp = SistarConstants.NUMBERX3_CODE_PATTERN)
   @Schema(example = "1", description = SistarConstants.ID_TYPE_DESCRIPTION, required = true)
   private String tipoDoc;

   @Pattern(regexp = SistarConstants.ID_PATTERN)
   @Schema(example = SistarConstants.ID_EXAMPLE, description = SistarConstants.ID_DESCRIPTION, required = true)
   private String num;

}
