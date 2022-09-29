package uy.com.md.mensajes.util;

import java.util.List;

import uy.com.md.mensajes.dto.ExternalResponseDto;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeDetail;

public final class Util {

	private Util() {
		super();
	}

	public static <G> ExternalResponseDto<G> internalToExternalResponse(InternalResponseDto<G> responseDto) {
		ExternalResponseDto<G> external = new ExternalResponseDto<>();
		external.setSuccess(responseDto.isResultado());
		responseDto.getMensajes().forEach(m -> external.getErrors().add(m));
		external.setInfo(responseDto.getRespuesta());
		return external;
	}

	public static <G> void mensajesToExternalResponse(ExternalResponseDto<G> responseDto, List<MensajeDetail> mensajes) {
		mensajes.forEach(m -> responseDto.getErrors().add(m));
	}

	public static <G> void mensajesToExternalResponse(ExternalResponseDto<G> responseDto, MensajeDetail... mensajes) {
		for (MensajeDetail m : mensajes) {
			responseDto.getErrors().add(m);
		}
	}

}
