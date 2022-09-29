package uy.com.md.jcard.dto.info;

import lombok.Data;

@Data
public class CustomerResponseDto {
  private String red;
  private ClienteDto cliente;
  private Integer sucursal;
  private ProductoDto producto;
  private TarjetaDto tarjeta;
  private String emisor;
}
