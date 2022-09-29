package uy.com.md.jcard.dto.in;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolicitudResponseDto {
	// private String success;
	private String realAccount;
	private String trace;
	private String errors;
	private String codigoAutorizacion;
	private String itc;
	private String codigoResultado;
}
