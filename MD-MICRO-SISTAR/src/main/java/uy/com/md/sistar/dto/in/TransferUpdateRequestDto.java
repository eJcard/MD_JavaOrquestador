package uy.com.md.sistar.dto.in;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class TransferUpdateRequestDto {
  String cuenta;
  String rrn;

  @JsonIgnore
  String canal;
}
