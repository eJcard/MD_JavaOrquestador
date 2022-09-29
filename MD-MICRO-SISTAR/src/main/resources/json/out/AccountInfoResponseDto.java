package uy.com.md.sistar.Schemas.json.out;

import lombok.Data;

@Data
public class AccountInfoResponseDto {
  Boolean success = true;
  AccountInfoDto info;
}
