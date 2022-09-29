package uy.com.md.sistar.Schemas.json.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import uy.com.md.sistar.util.SistarConstants;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelefonoDto {

   @Min(0)
   @Max(99)
   @Schema(required = true)
   private Integer tipoTel;

   @Pattern(regexp = SistarConstants.PHONE_NUMBER_PATTERN)
   @Schema(required = true)
   private String num;
}
