package uy.com.md.sistar.Schemas.json.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class BalanceResponseDto {
   @JsonInclude(JsonInclude.Include.NON_NULL)
   @Data
   public static class Info {
      Integer moneda;
      String nombre;
      String simbolo;
      Boolean excepciones = false;
      Long tefId;
      private List<Balance> balances;

      public Info() {
         balances = new ArrayList<Balance>();
      }
   }

   Boolean success = true;
   List<Info> info;

   public BalanceResponseDto() {
      info = new ArrayList<Info>();
   }
}
