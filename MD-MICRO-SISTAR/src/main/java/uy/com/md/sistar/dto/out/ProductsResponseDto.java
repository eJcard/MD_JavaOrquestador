package uy.com.md.sistar.dto.out;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * ProductsResponseDto
 */
@Data
public class ProductsResponseDto {

  @Data
  public static class Info {
    @JsonProperty("nombre")
    @JsonInclude(Include.NON_NULL)
    String name;
    
    @JsonProperty("nombre1")
    @JsonInclude(Include.NON_NULL)
    String name1;
    
    @JsonProperty("nombre2")
    @JsonInclude(Include.NON_NULL)
    String name2;
    
    @JsonProperty("apellido")
    @JsonInclude(Include.NON_NULL)
    String lastName;
    
    @JsonProperty("apellido2")
    @JsonInclude(Include.NON_NULL)
    String lastName2;  
    
    @JsonProperty("productos")
    List<ProductInfo> products;
  }

  Boolean success;
  Info info = new Info();
}