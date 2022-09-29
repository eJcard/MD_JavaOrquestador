package uy.com.md.jcard.service;


import uy.com.md.jcard.dto.in.movientos_extendidos.MovimientoExtendidoDto;
import uy.com.md.mensajes.dto.InternalResponseDto;

public interface MovimientosService {
    InternalResponseDto<MovimientoExtendidoDto> obtenerMovimientoExtendido(String token, String identificador);
}
