package uy.com.md.persona.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.persona.dto.rcor.VinculoRequestDto;
import uy.com.md.persona.dto.rcor.VinculoResponseDto;
import uy.com.md.persona.dto.rcor.VinculosDto;
import uy.com.md.persona.service.rcor.VinculoService;

import java.util.List;

@RestController
@RequestMapping("persona")
public class VinculoController {

    @Autowired
    VinculoService vinculoService;

    @PostMapping("crear-vinculo")
    public ResponseEntity<InternalResponseDto<VinculoResponseDto>> crearVinculo(@RequestBody VinculoRequestDto vinculoDto) throws Exception {
        return new ResponseEntity<>(vinculoService.crearVinculo(vinculoDto), HttpStatus.OK);
    }

    @PostMapping("obtener-vinculo")
    public ResponseEntity<InternalResponseDto<VinculoResponseDto>> obtenerVinculo(@RequestBody VinculoRequestDto vinculoDto) {
        return new ResponseEntity<>(vinculoService.obtenerVinculo(vinculoDto), HttpStatus.OK);
    }

    @PostMapping("obtener-vinculos")
    public ResponseEntity<InternalResponseDto<List<VinculoResponseDto>>> obtenerVinculos(@RequestBody VinculosDto vinculoDto) {
        return new ResponseEntity<>(vinculoService.obtenerVinculos(vinculoDto), HttpStatus.OK);
    }
}
