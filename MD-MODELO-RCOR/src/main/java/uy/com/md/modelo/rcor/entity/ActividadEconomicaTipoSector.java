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
public class ActividadEconomicaTipoSector implements Serializable {
	
	private static final long serialVersionUID = 637369716119210289L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "actividad_economica_tipo_sector_id", nullable = false)
	private Long actividadEconomicaTipoSectorId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_actividad_economica_tipo_id", nullable = false)
	private CodActividadEconomicaTipo codActividadEconomicaTipo;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_actividad_economica_sector_id", nullable = false)
	private CodActividadEconomicaSector codActividadEconomicaSector;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_actividad_economica_id", nullable = false)
	private CodActividadEconomica codActividadEconomica;

}

