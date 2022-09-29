package uy.com.md.sistar.dto.out;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import uy.com.md.sistar.util.SistarConstants;

/**
 * NewCardResponseDto
 */
@Data
public class NewCardResponseDto {

   private boolean success;

   @Schema(example = SistarConstants.ACCOUNT_NUMBER_EXAMPLE)
   @Size(min = 10, max = 12)
   @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
   private String realAccount;
}