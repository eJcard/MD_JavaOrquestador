package uy.com.md.common.movientos_extendidos;

import lombok.Data;

@Data
public class MovimientoExtendidoDto{
    private Long id;
    private String identificador;
    private InfoExtendidaDto infoExtendida;
}
