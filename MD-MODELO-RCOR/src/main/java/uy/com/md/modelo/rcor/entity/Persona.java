package uy.com.md.modelo.rcor.entity;

import lombok.Data;
import uy.com.md.common.model.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Persona extends Auditable implements Serializable {
	
    private static final long serialVersionUID = 637074234082165784L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "persona_id")
    private Integer id;

    private Integer codPaisDocId;

    private Integer codTipoDocId;

    @Column(length = 100)
    private String numDoc;

    @Column(nullable = false)
    private String denominacion;

    private Integer codPaisResidenciaId;

    private Integer codDepartamentoResidenciaId;

    private Integer codLocalidadResidenciaId;

    private Integer codBarrioResidenciaId;

    @Column(nullable = false, columnDefinition="tinyint(1) default 1")
    private Boolean personaActiva;

    @Column(length = 100)
    private String ucreacion;

    private LocalDateTime fcreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_tipo_persona_id", nullable = false)
    private CodTipoPersona codTipoPersonaId;

    @Column(length = 100)
    private String dirCalle;

    private String dirAmpliacion;

    private String dirNroPuerta;

    private String dirNroApartamento;

    private String dirCodigoPostal;

    @Column(length = 5)
    private String dirPiso;
}
