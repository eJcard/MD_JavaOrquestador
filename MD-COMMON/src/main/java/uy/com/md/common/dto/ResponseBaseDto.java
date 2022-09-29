package uy.com.md.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseBaseDto {

	@JsonProperty(value="Mensaje")
	private String mensaje;
    @JsonProperty(value="Exito")
    private boolean isExito;
    @JsonProperty(value="Errors")
    private List<ErrorDto> errors;

}
