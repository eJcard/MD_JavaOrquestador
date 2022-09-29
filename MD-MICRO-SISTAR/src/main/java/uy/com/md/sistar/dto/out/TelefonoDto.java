package uy.com.md.sistar.dto.out;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import uy.com.md.sistar.util.SistarConstants;

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
