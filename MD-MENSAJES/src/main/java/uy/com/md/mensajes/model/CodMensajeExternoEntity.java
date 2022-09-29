package uy.com.md.mensajes.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import uy.com.md.mensajes.util.Constantes;

@Data
@Entity
@Table(name = "cod_mensaje_externo")
@IdClass(CodMensajeExternoId.class)
public class CodMensajeExternoEntity implements Serializable {

	private static final long serialVersionUID = 637078700708565775L;

	@Id
	@Column(name = "origen", length = 100, nullable = false)
	private String origen;

	@Id
	@Column(name = "codigo", length = Constantes.COLUMN_CODIGO_LENGTH, nullable = false)
	private String codigo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_negocio", nullable = false)
	private CodMensajeNegocioEntity codMensajeNegocio;

	@Column(name = "mensaje_externo", length = 1000, nullable = false)
	private String mensajeExterno;

	@Column(name = "uact", length = 50, nullable = false)
	private String uact;

	@Column(name = "fact", nullable = false)
	private LocalDateTime fact;

}
