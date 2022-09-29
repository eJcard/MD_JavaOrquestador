package uy.com.md.persona.model.rcor;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.*;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import uy.com.md.common.model.Auditable;

@Entity
@DynamicUpdate
@Data
@IdClass(PersonaId.class)
public class PersonaFisica extends Auditable implements Serializable {

    private static final long serialVersionUID = 639074230482165784L;

    @Id
    private Integer personaId;

    @Column(length = 100)
    private String primerNombre;

    @Column(length = 100)
    private String segundoNombre;

    @Column(length = 100)
    private String primerApellido;

    @Column(length = 100)
    private String segundoApellido;

    private LocalDate fechaNac;

    @Column(length = 3)
    private String codSexoId;

    private Integer codPaisNacId;

    private Character codEstadoCivilId;

    private Integer actividadEconomicaTipoSectorId;

    @Id
    private String origenId;

}
