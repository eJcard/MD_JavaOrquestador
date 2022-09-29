package uy.com.md.sistar.dto.out;

import lombok.Data;

@Data
public class TokenResponse {

   private String access_token;

   private String token_type;

   private Integer expires_in;

}
