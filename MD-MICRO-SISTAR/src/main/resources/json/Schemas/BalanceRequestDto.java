package uy.com.md.sistar.Schemas.json.in;

import lombok.Data;

@Data
public class BalanceRequestDto {

   private String country;

   private String document;

   private String documentType;

   private String account;

}
