package uy.com.md.global.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uy.com.md.common.util.Constantes;
import uy.com.md.global.dto.CambioRequestDto;
import uy.com.md.global.service.CambioService;
import uy.com.md.global.soap.client.aamdsoaprecargargp.AmdSoapRecargarGPExecuteResponse;
import uy.com.md.mensajes.dto.InternalResponseDto;

@RestController
@RequestMapping("global")
public class CambioController {

    @Autowired
    CambioService cambioService;

    @PostMapping("cambio-moneda")
    public ResponseEntity<InternalResponseDto<AmdSoapRecargarGPExecuteResponse>> cambio(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestHeader(Constantes.REQUEST_ID) String uuid, @RequestBody CambioRequestDto cambioRequestDto) {
        return new ResponseEntity<>(cambioService.cambio(token, uuid, cambioRequestDto), HttpStatus.OK);
    }
}
