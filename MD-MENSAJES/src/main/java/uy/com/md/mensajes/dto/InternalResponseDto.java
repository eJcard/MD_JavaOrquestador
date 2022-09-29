package uy.com.md.mensajes.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Clase contenedora para las respuestas de los Micro-WS internos que no van a
 * ser expuestos a MiDinero
 */

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InternalResponseDto<T> implements Serializable {

	private static final long serialVersionUID = 4840530493452509480L;
	private T respuesta;
	private boolean resultado;
	private List<MensajeDetail> mensajes;

	public InternalResponseDto() {
		this(null, true, new ArrayList<>());
	}

	public InternalResponseDto(T respuesta) {
		this(respuesta, true, new ArrayList<>());
	}

	public InternalResponseDto(T respuesta, boolean resultado) {
		this(respuesta, resultado, new ArrayList<>());
	}

	public void addMensaje(MensajeDetail mensaje) {
		this.mensajes.add(mensaje);
	}

}
