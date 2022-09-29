package uy.com.md.sistar.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferResponseDto {

  String codigoAutorizacion;
  String codigoResultado;
  Boolean success;
  String itc;
  Long cobraCupon;
  Long consuCupon;
}
