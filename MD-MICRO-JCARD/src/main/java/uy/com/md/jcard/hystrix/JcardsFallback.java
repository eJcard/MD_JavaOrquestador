package uy.com.md.jcard.hystrix;

import org.springframework.stereotype.Component;

import uy.com.md.jcard.client.JcardClient;
import uy.com.md.jcard.config.JcardRequestException;
import uy.com.md.jcard.dto.in.TransferenciaRequestDto;
import uy.com.md.jcard.dto.in.movimientos.JcardMovimientosParamsDto;
import uy.com.md.jcard.dto.out.*;
import uy.com.md.jcard.dto.in.movimientos.TransaccionesResponseDto;

@Component
public class JcardsFallback implements JcardClient {

	@Override
	public CardsResponseDto cards(String bearerToken, String version, String consumerId, CardsRequestDto request) {
		return null;
	}

	@Override
	public CardsResponseDto cardDelivery(String bearerToken, String version, String consumerId, EntregaRequestDto entregaRequestDto, String nroSobre) {
		return null;
	}

	@Override
	public CardsResponseDto cardCredits(String bearerToken, String version, String consumerId, RecargaRequestOutDto creditoRequestDto) throws JcardRequestException {
		return null;
	}

	@Override
	public TransaccionesResponseDto cardTransactions(String bearerToken, String version, String consumerId, String realAccount, JcardMovimientosParamsDto movimientosParamsDto) throws JcardRequestException {
		return null;
	}
	
	@Override
	public CardsResponseDto cardTransfersC2C(String bearerToken, String version, String consumerId, TransferenciaRequestDto transferenciaRequestDto) throws JcardRequestException {
		return null;
	}

	@Override
	public CardsResponseDto cardDebits(String bearerToken, String version, String consumerId, PagoServiciosRequestOutDto pagoServiciosRequestOutDto) throws JcardRequestException {
		return null;
	}
}
