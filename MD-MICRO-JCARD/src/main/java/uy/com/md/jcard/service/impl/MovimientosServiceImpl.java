package uy.com.md.jcard.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uy.com.md.jcard.client.MovimientosClient;
import uy.com.md.jcard.dto.in.movientos_extendidos.MovimientoExtendidoDto;
import uy.com.md.jcard.service.MovimientosService;
import uy.com.md.mensajes.dto.InternalResponseDto;

@Service
public class MovimientosServiceImpl implements MovimientosService {

    @Autowired
    MovimientosClient movimientosClient;

    @Override
    public InternalResponseDto<MovimientoExtendidoDto> obtenerMovimientoExtendido(String token, String identificador) {
        return movimientosClient.obtenerMovimientoExtendido(token, identificador);
    }
}
