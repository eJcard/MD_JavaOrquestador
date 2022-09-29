package uy.com.md.mensajes.enumerated;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import uy.com.md.mensajes.dto.MensajeDetail;

@Deprecated
@AllArgsConstructor
public enum MensajeDetails {

	/**
	 * GENERAL_ERROR 0 a 999
	 */
	GENERAL_ERROR("", "1", "ERROR inesperado", MensajeType.ERROR),

	/**
	 * ERROR_GENERICO_* de 1000 a 1999
	 */
	ERROR_GENERICO("", "1000", "ERROR GENERICO", MensajeType.ERROR),

	/**
	 * MD_AUTORIZACION_* de 2000 a 2999
	 */
	MD_AUTORIZACION_ERROR("MD_AUTORIZACION", "2000", "ERROR EN MD AUTORIZACION", MensajeType.ERROR),

	/**
	 * MD_CUMPLIMIENTO_* de 3000 a 3999
	 */
	MD_CUMPLIMIENTO_ERROR("MD_CUMPLIMIENTO", "3000", "ERROR EN MD CUMPLIMIENTO", MensajeType.ERROR),

	/**
	 * MD_JCARD_* de 4000 a 4999
	 */
	MD_JCARD_ERROR("MD_JCARD", "4000", "ERROR EN MD JCARD", MensajeType.ERROR),
	MD_JCARD_ERROR_CONSUMIENDO("MD_JCARD", "4001", "ERROR EN MD JCARD NULL RESPONSE", MensajeType.ERROR),

	/**
	 * MD_GLOBAL_* de 5000 a 5999
	 */
	MD_GLOBAL_ERROR("MD_GLOBAL", "5000", "ERROR EN MD GLOBAL", MensajeType.ERROR),

	/**
	 * MD_CONTRATO_* de 6000 a 6999
	 */
	MD_CONTRATO_ERROR("MD_CONTRATO", "6000", "ERROR EN MD CONTRATO", MensajeType.ERROR),

	/**
	 * MD_PERSONA_* de 7000 a 7999
	 */
	MD_PERSONA_ERROR("MD_PERSONA", "7000", "ERROR EN MD PERSONA", MensajeType.ERROR),

	/**
	 * MD_SEGURIDAD_* de 8000 a 8999
	 */
	MD_SEGURIDAD_ERROR("MD_SEGURIDAD", "8000", "ERROR EN MD SEGURIDAD", MensajeType.ERROR),
	MD_SEGURIDAD_ERROR_AUTORIZADO("MD_SEGURIDAD", "8001", "NO ESTA AUTORIZADO A OPERAR", MensajeType.ERROR),
	MD_SEGURIDAD_ERROR_TOKEN("MD_SEGURIDAD", "8002", "TOKEN INVALIDO", MensajeType.ERROR),
	MD_SEGURIDAD_ERROR_IDEMPOTENCIA("MD_SEGURIDAD", "8003", "EL REQUEST ID YA FUE UTILIZADO Y SE EXCEDIÓ EL TIEMPO DE EJECUCIÓN MÁXIMO", MensajeType.ERROR),

	/**
	 * MD_SISTARBANC_* de 10000 a 10999
	 */
	MD_SISTARBANC_ERROR("MD_SISTARBANC", "10000", "ERROR EN MD SISTARBANC", MensajeType.ERROR),

	/**
	 * MD_BASICOPRESENCIAL_* de 11000 a 11999
	 */
	MD_BASICOPRESENCIAL_ERROR("MD_BASICOPRESENCIAL", "11000", "ERROR EN MD BÁSICO PRESENCIAL", MensajeType.ERROR),

	/**
	 * MD_BASICODIGITAL_* de 12000 a 12999
	 */
	MD_BASICODIGITAL_ERROR("MD_BASICODIGITAL", "12000", "ERROR EN MD BÁSICO DIGITAL", MensajeType.ERROR);



	private final String origen;

	private final String codigo;

	private final String mensaje;

	private final MensajeType tipo;

	private static final Map<String, MensajeDetails> CODIGOS = new HashMap<>();

	static {
		Arrays.asList(values()).forEach(e -> CODIGOS.put(e.codigo, e));
	}

	public String getCodigo() {
		return this.codigo;
	}

	public static MensajeDetails valueOfCodigo(String codigo) {
		return CODIGOS.get(codigo);
	}

	public MensajeDetail getMensajeDetail(String mensaje) {
		return new MensajeDetail(this.origen, this.codigo, String.format("%s: %s", this.mensaje, mensaje), this.tipo);
	}

	public MensajeDetail getMensajeDetail() {
		return new MensajeDetail(this.origen, this.codigo, this.mensaje, this.tipo);
	}
}
