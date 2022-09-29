package uy.com.md.sistar.Schemas.json.out;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import uy.com.md.sistar.util.SistarConstants;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class AccountResponseDto {

   @Parameter(example = "370100000113", required = true)
   @Size(min = 10, max = 18)
   @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
   private String realAccount;

}
