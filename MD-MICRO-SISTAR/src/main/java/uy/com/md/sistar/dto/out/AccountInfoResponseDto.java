package uy.com.md.sistar.dto.out;

import lombok.Data;

@Data
public class AccountInfoResponseDto {
  Boolean success = true;
  AccountInfoDto info;
}
