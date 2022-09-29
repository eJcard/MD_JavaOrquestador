package uy.com.md.mensajes.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase contenedora para las respuestas de los Micro-WS expuesto a MiDinero
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalResponseDto<T> implements Serializable {

	private static final long serialVersionUID = 6549409261471764505L;
	private Boolean success;
	private T info;
	private Set<Object> errors = new HashSet<>();
	private String trace;

}
