package uy.com.md.sistar.dto.out;

import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReverseResponseDto {

   private Boolean success;

   @Schema(example = "810161")
   @Size(max = 6)
   private String reversalRrn;

   String itc;
}
