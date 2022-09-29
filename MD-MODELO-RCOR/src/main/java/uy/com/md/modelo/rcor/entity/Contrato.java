package uy.com.md.modelo.rcor.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import uy.com.md.common.model.Auditable;

@Entity
@Data
public class Contrato extends Auditable implements Serializable {
	
	private static final long serialVersionUID = 637758694082165784L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contrato_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Column (name = "fecha_desde")
    private LocalDate fechaDesde;

    @Column (name = "fecha_hasta")
    private LocalDate fechaHasta;

    @Column(length = 100)
    private String descripcion;

    @Column(length = 10000)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_estado_contrato_id", nullable = false)
    private CodEstadoContrato codEstadoContrato;

}
