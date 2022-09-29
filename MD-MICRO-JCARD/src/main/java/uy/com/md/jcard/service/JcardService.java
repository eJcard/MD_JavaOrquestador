package uy.com.md.jcard.service;

import org.springframework.stereotype.Service;

import uy.com.md.jcard.dto.in.*;
import uy.com.md.jcard.dto.in.movimientos.JcardMovimientosParamsDto;
import uy.com.md.jcard.dto.in.PagoServiciosRequestDto;
import uy.com.md.jcard.dto.out.EntregaRequestDto;
import uy.com.md.mensajes.dto.InternalResponseDto;

import java.util.Optional;

@Service
public interface JcardService {

    InternalResponseDto<SolicitudResponseDto> getCards(String token, SolicitudRequestDto jcardsRequestDto);

    InternalResponseDto<SolicitudResponseDto> cardDelivery(String token, EntregaRequestDto entregaRequestDto, String nroSobre);

    InternalResponseDto<SolicitudResponseDto> cardCredits(String token, RecargaRequestDto recargaRequestDto);

    InternalResponseDto cardTransactions(String token, String cuenta, Optional<JcardMovimientosParamsDto> movimientosRequestDto);

    InternalResponseDto<Boolean> cardTransfers(String token, TransferenciaRequestDto transferenciaRequestDto);
    
    InternalResponseDto<SolicitudResponseDto> cardDebits(String token, PagoServiciosRequestDto pagoServiciosRequestDto);

    InternalResponseDto customerInfo(String token, String cuenta);
}
