package uy.com.md.jcard.dto.in.movientos_extendidos;

import lombok.Data;

@Data
public class MovimientoExtendidoDto{
    private Long id;
    private String identificador;
    private InfoExtendidaDto infoExtendida;
}
