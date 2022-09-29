package uy.com.md.modelo.rcor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class CodDepartamento implements Serializable {
	
	private static final long serialVersionUID = 637369752557163120L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cod_departamento_id", nullable = false)
	private Long codDepartamentoId;

	@Column(name = "nombre", length = 200, nullable = false)
	private String nombre;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_pais_id", nullable = false)
	private CodPais codPais;

}
