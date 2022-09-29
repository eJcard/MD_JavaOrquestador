package uy.com.md.modelo.rcor.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class CodBarrio implements Serializable {

	private static final long serialVersionUID = 637369733674083714L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cod_barrio_id", nullable = false)
	private Long codBarrioId;

	@Column(name = "nombre", length = 200, nullable = false)
	private String nombre;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_localidad_id", nullable = false)
	private CodLocalidad codLocalidad;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_departamento_id", nullable = false)
	private CodDepartamento codDepartamento;
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_pais_id", nullable = false)
	private CodPais codPais;

}
