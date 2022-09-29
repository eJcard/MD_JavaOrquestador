package uy.com.md.sistar.dto.out;

import java.util.List;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;
import lombok.Getter;
import uy.com.md.sistar.dto.CodeNameTuple;
import uy.com.md.sistar.util.SistarConstants;

@Data
public class ProductsInfoToRemove {

   protected List<ProductInfo> info;

   @Data
   public static class ProductInfo {

      @Getter
      public enum Status {
         ACTIVE("active"),
         INACTIVE("suspended"),
         UNKNOWN(null);

         private final String status;

         @JsonCreator
         Status(String status) {
            this.status = status;
         }

         @JsonValue
         public String getStatus() {
            return this.status;
         }
      }

      private Status estado;

      @Pattern(regexp = "\\d{4}")
      @Schema(example = "1234")
      private String ultimosCuatro;

      @Pattern(regexp = SistarConstants.ACCOUNT_NUMBER_PATTERN)
      @Schema(example = SistarConstants.ACCOUNT_NUMBER_EXAMPLE)
      private String cuenta;

      private CodeNameTuple grupoAfinidad;

      private CodeNameTuple producto;

      @Size(max = 999)
      @Schema(required = true, example = "36")
      private Long emisor;
   }
}
