package uy.com.md.jcard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import uy.com.md.jcard.config.JcardClientConfig;
import uy.com.md.jcard.dto.in.movientos_extendidos.MovimientoExtendidoDto;
import uy.com.md.jcard.util.Constantes;
import uy.com.md.mensajes.dto.InternalResponseDto;

@FeignClient(value = "movimientos", url = "${movimientos.endpoint}", configuration = JcardClientConfig.class)
public interface MovimientosClient {

    @GetMapping("movimientos-extendidos/obtener/{identificador}")
    InternalResponseDto<MovimientoExtendidoDto> obtenerMovimientoExtendido(@RequestHeader(Constantes.AUTH_TOKEN) String token, @PathVariable(name = "identificador") String identificador);

}
