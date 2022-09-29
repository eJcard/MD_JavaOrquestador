package uy.com.md.persona.model.rcor;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import uy.com.md.common.model.Auditable;

@Entity
@DynamicUpdate
@Data
@IdClass(PersonaId.class)
public class PersonaJuridica extends Auditable implements Serializable {

    private static final long serialVersionUID = 639074230482165784L;

    @Id
    private Integer personaId;

    private String razonSocial;

    private String nombreFantasia;

    private Integer numBps;

    private Integer codTipoEmpresaId;

    private Integer codActividadEconomicaId;

    @Id
    private String origenId;


}
