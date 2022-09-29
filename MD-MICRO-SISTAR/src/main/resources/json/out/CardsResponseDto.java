package uy.com.md.sistar.Schemas.json.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardsResponseDto {

   private Boolean success;

   private String realAccount;

   private Long id;

   @JsonProperty("excepciones")
   private List<ExceptionFraude> exceptionFraude = new ArrayList<>();

   @JsonProperty("excepcionesActivas")
   private Boolean hasExceptions;

   private String trace;

   private String errors;

   @JsonProperty("excepcionesActivas")
   public Boolean getHasExceptions(){
      return this.exceptionFraude != null && this.exceptionFraude.size() > 0;
   }
}
