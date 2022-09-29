package uy.com.md.mensajes.model;

import lombok.Data;
import uy.com.md.mensajes.util.Constantes;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "cod_mensaje_negocio")
public class CodMensajeNegocioEntity implements Serializable {

	private static final long serialVersionUID = 636975996264300901L;

	@Id
	@Column(name = "codigo_negocio", length = Constantes.COLUMN_CODIGO_LENGTH, nullable = false)
	private String codigoNegocio;

	@Column(name = "mensaje_negocio", length = 1000, nullable = false)
	private String mensajeNegocio;

	@Column(name = "severidad", length = 1, nullable = false)
	private String severidad;

	@Column(name = "uact", length = 50, nullable = false)
	private String uact;

	@Column(name = "fact", nullable = false)
	private LocalDateTime fact;

}