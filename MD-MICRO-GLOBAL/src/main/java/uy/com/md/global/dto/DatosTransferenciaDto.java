package uy.com.md.global.dto;

import lombok.Data;

@Data
public class DatosTransferenciaDto {
	private String cuenta;
	private BancoDto banco;
	private String nombre;
}
