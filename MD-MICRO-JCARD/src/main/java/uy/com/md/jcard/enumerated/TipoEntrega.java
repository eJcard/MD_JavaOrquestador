package uy.com.md.jcard.enumerated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum TipoEntrega {

	@JsonProperty("alta")
	ALTA, 
	@JsonProperty("reimpresion")
	REIMPRESION, 
	@JsonProperty("renovacion")
	RENOVACION, 
	@JsonProperty("noEspecificado")
	NOESPECIFICADO,
	@JsonProperty("")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	NONE;
}
