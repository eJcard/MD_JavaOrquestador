package uy.com.md.modelo.rcor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class CodPais implements Serializable {

	private static final long serialVersionUID = 637369753356018595L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cod_pais_id", nullable = false)
	private Long codPaisId;

	@Column(name = "alpha2", length = 2, nullable = false)
	private String alpha2;

	@Column(name = "nombre", length = 200, nullable = false)
	private String nombre;

	@Column(name = "cod_pais_id_rp", nullable = true)
	private Long codPaisIdRp;

}
