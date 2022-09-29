package uy.com.md.mensajes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uy.com.md.mensajes.interfaces.MensajeOrigenInterface;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeOrigen implements MensajeOrigenInterface {

	private String origen;

	private String codigo;

	private String mensajeExterno;
}
