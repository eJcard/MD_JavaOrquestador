package uy.com.md.common.enumerated;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

///**
// * @deprecated en su lugar debe empesar a usarse las clases y Enum de Mensaje.
// */
//@Deprecated
@JsonFormat
@Deprecated
public enum MDErrorMessage {

	// @todo: Estsos errores deber'ian gestionarse desde la base de datos

	/* GENERAL_ERROR 0 a 999 */
	DEFAULT_MESSAGE(1, "Ha ocurrido un error inesperado"),

	/* ERROR_GENERICO_* de 1000 a 1999 */
	ERROR_GENERICO(1000, "Erro generico"),

	/* MD_AUTORIZACION_* de 2000 a 2999 */
	MD_AUTORIZACION_ERROR(2000, "Error en MD Autorizacion"),

	/* MD_CUMPLIMIENTO_* de 3000 a 3999 */
	MD_CUMPLIMIENTO_ERROR(3000, "Error en MD Cumplimiento"),

	/* MD_JCARD_* de 4000 a 4999 */
	MD_JCARD_ERROR(4000, "Error en MD JCARD"), 
	MD_JCARD_ERROR_CONSUMIENDO(4001, "Error en MD JCARD arrojado por el WS jCard"),

	/* MD_GLOBAL_* de 5000 a 5999 */
	MD_GLOBAL_ERROR(5000, "Error en MD GLOBAL"),

	/* MD_CONTRATO_* de 6000 a 6999 */
	MD_CONTRATO_ERROR(6000, "Error en MD CONTRATO"),

	/* MD_PERSONA_* de 7000 a 7999 */
	MD_PERSONA_ERROR(7000, "Error en MD PERSONA"),

	/* MD_SISTAR_* de 8000 a 8999 */
	MD_SISTAR_ERROR(8000, "Error en MD SISTAR");

	private int code;

	private String message;

	MDErrorMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	@JsonValue
	public String getMessage() {
		return message;
	}

	public static MDErrorMessage getByValue(int code) {
		return Arrays.asList(values()).stream().filter(message -> code == message.getCode()).findAny().orElse(null);
	}
}
