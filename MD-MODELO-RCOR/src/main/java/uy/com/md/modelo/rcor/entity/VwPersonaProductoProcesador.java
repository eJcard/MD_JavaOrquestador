package uy.com.md.modelo.rcor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class VwPersonaProductoProcesador implements Serializable {

	private static final long serialVersionUID = 637369910151029689L;

	@Id
	@Column(name = "producto_id", nullable = true)
	private Integer productoId;

	@Column(name = "cod_pais_doc_id", nullable = true)
	private Integer codPaisDocId;

	@Column(name = "cod_tipo_doc_id", nullable = true)
	private Integer codTipoDocId;

	@Column(name = "num_doc", length = 100, nullable = true)
	private String numDoc;

	@Column(name = "procesador", length = 100, nullable = true)
	private String procesador;

	@Column(name = "numero_cuenta", length = 255, nullable = true)
	private String numeroCuenta;

	@Column(name = "codigo", length = 10, nullable = true)
	private String codigo;

}
