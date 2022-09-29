package uy.com.md.jcard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import uy.com.md.jcard.config.JcardClientConfig;
import uy.com.md.jcard.config.JcardRequestException;
import uy.com.md.jcard.dto.in.TransferenciaRequestDto;
import uy.com.md.jcard.dto.in.movimientos.JcardMovimientosParamsDto;
import uy.com.md.jcard.dto.out.*;
import uy.com.md.jcard.dto.in.movimientos.TransaccionesResponseDto;
import uy.com.md.jcard.util.Constantes;

@FeignClient(value = "cards", url = "${jcard.endpoint}", configuration = JcardClientConfig.class)
public interface JcardClient {

    @PostMapping("/cards")
    CardsResponseDto cards(@RequestHeader(Constantes.AUTH_TOKEN) String bearerToken, @RequestHeader(Constantes.JCARD_HEADERS_VERSION) String version, @RequestHeader(Constantes.JCARD_HEADERS_CONSUMER_ID) String consumerId, @RequestBody CardsRequestDto request) throws JcardRequestException;

    @PutMapping("/cards/{id}/deliver")
    CardsResponseDto cardDelivery(@RequestHeader(Constantes.AUTH_TOKEN) String bearerToken, @RequestHeader(Constantes.JCARD_HEADERS_VERSION) String version, @RequestHeader(Constantes.JCARD_HEADERS_CONSUMER_ID) String consumerId, @RequestBody EntregaRequestDto entregaRequestDto, @PathVariable(name = "id") String nroSobre) throws JcardRequestException;

    @PostMapping("/cards/credits")
    CardsResponseDto cardCredits(@RequestHeader(Constantes.AUTH_TOKEN) String bearerToken, @RequestHeader(Constantes.JCARD_HEADERS_VERSION) String version, @RequestHeader(Constantes.JCARD_HEADERS_CONSUMER_ID) String consumerId, @RequestBody RecargaRequestOutDto creditoRequestDto) throws JcardRequestException;

    @GetMapping("/cards/{realAccount}/transactions")
    TransaccionesResponseDto cardTransactions(@RequestHeader(Constantes.AUTH_TOKEN) String bearerToken, @RequestHeader(Constantes.JCARD_HEADERS_VERSION) String version, @RequestHeader(Constantes.JCARD_HEADERS_CONSUMER_ID) String consumerId, @PathVariable(name = "realAccount") String realAccount, @SpringQueryMap JcardMovimientosParamsDto movimientosParamsDto) throws JcardRequestException;
    
    @PostMapping("/cards/transfers")
    CardsResponseDto cardTransfersC2C(@RequestHeader(Constantes.AUTH_TOKEN) String bearerToken, @RequestHeader(Constantes.JCARD_HEADERS_VERSION) String version, @RequestHeader(Constantes.JCARD_HEADERS_CONSUMER_ID) String consumerId, @RequestBody TransferenciaRequestDto request) throws JcardRequestException;

    @PostMapping("/cards/debits")
    CardsResponseDto cardDebits(@RequestHeader(Constantes.AUTH_TOKEN) String bearerToken, @RequestHeader(Constantes.JCARD_HEADERS_VERSION) String version, @RequestHeader(Constantes.JCARD_HEADERS_CONSUMER_ID) String consumerId, @RequestBody PagoServiciosRequestOutDto pagoServiciosRequestOutDto) throws JcardRequestException;

}
