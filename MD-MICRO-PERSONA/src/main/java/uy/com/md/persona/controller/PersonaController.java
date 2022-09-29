package uy.com.md.persona.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uy.com.md.common.util.Constantes;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.persona.dto.rcor.*;
import uy.com.md.persona.service.rcor.PersonaService;

@RestController
@RequestMapping("persona")
public class PersonaController {

    @Autowired
    PersonaService personaService;

    @PostMapping("crear")
    public ResponseEntity<InternalResponseDto<PersonaDto>> crearPersona(@RequestHeader(Constantes.MD_ORIGEN) String origen, @RequestBody PersonaDto personaDto) {
        return new ResponseEntity<>(personaService.crearPersona(origen, personaDto), HttpStatus.OK);
    }

    @PostMapping("actualizar/{pais}/{tipoDocumento}/{numeroDocumento}")
    public ResponseEntity<InternalResponseDto<PersonaDto>> actualizarPersona(@RequestHeader(Constantes.MD_ORIGEN) String origen, @RequestBody PersonaDto personaDto, @PathVariable String pais, @PathVariable String tipoDocumento, @PathVariable String numeroDocumento) {
        return new ResponseEntity<>(personaService.crearPersona(origen, personaDto), HttpStatus.OK);
    }

    @PostMapping("actualizar/{personaId}")
    public ResponseEntity<Void> actualizar(@RequestHeader(Constantes.MD_ORIGEN) String origen, @PathVariable Integer personaId, @RequestBody PersonaDto request) {
        personaService.actualizarPersona(origen, personaId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("obtener/{personaId}")
    public ResponseEntity<PersonaDto> obtener(@RequestHeader(Constantes.MD_ORIGEN) String origen, @PathVariable Integer personaId) {
        return new ResponseEntity<>(personaService.obtener(origen, personaId), HttpStatus.OK);
    }

    @PostMapping("obtener")
    public ResponseEntity<PersonaDto> obtenerPersona(@RequestHeader(Constantes.MD_ORIGEN) String origen, @RequestBody ObtPersonaPorDocumentoRequestDto obtPersonaPorDocumentoRequestDto) {
        return new ResponseEntity<>(personaService.obtener(origen, obtPersonaPorDocumentoRequestDto.getPaisDoc(), obtPersonaPorDocumentoRequestDto.getTipoDoc(), obtPersonaPorDocumentoRequestDto.getNumDoc()), HttpStatus.OK);
    }

    @DeleteMapping("eliminar/{personaId}")
    public ResponseEntity<Boolean> eliminar(@RequestHeader(Constantes.MD_ORIGEN) String origen, @PathVariable Integer personaId) {
        return new ResponseEntity<>(personaService.eliminar(origen, personaId), HttpStatus.OK);
    }
}
