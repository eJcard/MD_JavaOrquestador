package uy.com.md.sistar.Schemas.json.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReverseResponseDto {

   private Boolean success;

   @Schema(example = "810161")
   @Size(max = 6)
   private String reversalRrn;

   String itc;
}
