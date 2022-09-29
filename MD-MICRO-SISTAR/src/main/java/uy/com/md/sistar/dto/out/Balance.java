package uy.com.md.sistar.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Balance {

   @Schema(example = "1000.25", description = "Amount available")
   private Double balance;
   
   @Schema(example = "858", description = "Currency code/symbol")
   @JsonProperty("codigo")
   private String moneda;
   
   @Schema(example = "Pesos", description = "Currency name")
   private String nombre;

}
