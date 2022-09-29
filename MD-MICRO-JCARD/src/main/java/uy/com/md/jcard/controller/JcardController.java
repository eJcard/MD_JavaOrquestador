package uy.com.md.jcard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import uy.com.md.jcard.dto.in.*;
import uy.com.md.jcard.dto.in.movimientos.JcardMovimientosParamsDto;
import uy.com.md.jcard.dto.in.PagoServiciosRequestDto;
import uy.com.md.jcard.dto.out.EntregaRequestDto;
import uy.com.md.jcard.service.JcardService;
import uy.com.md.jcard.util.Constantes;
import uy.com.md.mensajes.dto.InternalResponseDto;

import java.util.Optional;

@RestController
@Validated
@RequestMapping("jCard/cards")
public class JcardController {

    @Autowired
    JcardService jCardService;

    @PostMapping
    public ResponseEntity<InternalResponseDto<SolicitudResponseDto>> getCards(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestBody SolicitudRequestDto jcardsRequestDto) {
        return new ResponseEntity<>(jCardService.getCards(token, jcardsRequestDto), HttpStatus.OK);
    }

    @PutMapping("/{nroSobre}/deliver")
    public ResponseEntity<InternalResponseDto<SolicitudResponseDto>> cardDelivery(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestBody EntregaRequestDto entregaRequestDto, @PathVariable String nroSobre) {
        return new ResponseEntity<>(jCardService.cardDelivery(token, entregaRequestDto, nroSobre), HttpStatus.OK);
    }

    @PostMapping("/credits")
    public ResponseEntity<InternalResponseDto<SolicitudResponseDto>> cardCredits(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestBody RecargaRequestDto recargaRequestDto) {
        return new ResponseEntity<>(jCardService.cardCredits(token, recargaRequestDto), HttpStatus.OK);
    }

    @GetMapping("/{cuenta}/transactions")
    public ResponseEntity<InternalResponseDto> cardTransactions(@RequestHeader(Constantes.AUTH_TOKEN) String token, @PathVariable String cuenta, @RequestBody Optional<JcardMovimientosParamsDto> movimientosRequestDto) {
        return new ResponseEntity<>(jCardService.cardTransactions(token, cuenta, movimientosRequestDto), HttpStatus.OK);
    }
    
    @PostMapping("/transfers")
    public ResponseEntity<InternalResponseDto<Boolean>> cardTransfers(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestBody TransferenciaRequestDto transferenciaRequestDto) {
        return new ResponseEntity<>(jCardService.cardTransfers(token, transferenciaRequestDto), HttpStatus.OK);
    }

    @PostMapping("/debits")
    public ResponseEntity<InternalResponseDto<SolicitudResponseDto>> cardDebits(@RequestHeader(Constantes.AUTH_TOKEN) String token, @RequestBody PagoServiciosRequestDto pagoServiciosRequestDto) {
        return new ResponseEntity<>(jCardService.cardDebits(token, pagoServiciosRequestDto), HttpStatus.OK);
    }

    @GetMapping("/customers/{cuenta}")
    public ResponseEntity<InternalResponseDto> customerInfo(@RequestHeader(Constantes.AUTH_TOKEN) String token, @PathVariable String cuenta) {
        return new ResponseEntity<>(jCardService.customerInfo(token, cuenta), HttpStatus.OK);
    }
}
