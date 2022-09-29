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
public class CodEstadoCivil implements Serializable {

	private static final long serialVersionUID = 637369744624548451L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cod_estado_civil_id", length = 1, nullable = false)
	private String codEstadoCivilId;

	@Column(name = "nombre", length = 100, nullable = false)
	private String nombre;

}
