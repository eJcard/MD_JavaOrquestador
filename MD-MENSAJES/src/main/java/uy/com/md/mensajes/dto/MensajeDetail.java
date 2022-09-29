package uy.com.md.mensajes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uy.com.md.mensajes.enumerated.MensajeType;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDetail implements Serializable {

	private static final long serialVersionUID = 358187834403402312L;

	@JsonProperty("origen")
	private String origen;

	@JsonProperty("codigo")
	private String codigo;

	@JsonProperty("mensaje")
	private String mensaje;

	@JsonProperty("tipo")
	private MensajeType tipo;

	@Override
	public String toString() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

}
