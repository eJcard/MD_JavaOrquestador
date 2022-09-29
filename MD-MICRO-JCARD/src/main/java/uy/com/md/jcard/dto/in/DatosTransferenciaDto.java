package uy.com.md.jcard.dto.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import uy.com.md.jcard.dto.in.movimientos.BancoDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatosTransferenciaDto {
	private String cuenta;
	private BancoDto banco;
	private String nombre;
}

