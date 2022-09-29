package uy.com.md.mensajes.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodMensajeExternoId implements Serializable {

	private static final long serialVersionUID = 8629446113113511442L;

	@Id
	@Column(name = "origen", length = 100, nullable = false)
	private String origen;

	@Id
	@Column(name = "codigo", length = 100, nullable = false)
	private String codigo;
}
