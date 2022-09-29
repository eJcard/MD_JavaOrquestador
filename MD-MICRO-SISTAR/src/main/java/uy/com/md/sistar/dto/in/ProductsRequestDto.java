package uy.com.md.sistar.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
public class ProductsRequestDto {

   @Schema(example = "36")
   private Long issuer;

   private Long product;

   private String country;

   private String document;

   private String documentType;

}
