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
public class CodActividadEconomicaTipo implements Serializable {
	
	private static final long serialVersionUID = 637369733113102703L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cod_actividad_economica_tipo_id", nullable = false)
	private Long codActividadEconomicaTipoId;

	@Column(name = "nombre", length = 100, nullable = false)
	private String nombre;
}
