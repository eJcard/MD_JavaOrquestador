package uy.com.md.persona.model.rcor;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import uy.com.md.common.model.Auditable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class Direccion extends Auditable implements Serializable {

    private static final long serialVersionUID = 636074234082165784L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="direccion_id")
    private Long id;

    private Integer personaId;

    private String origenId;

    private Integer codPaisResidenciaId;

    private Integer codDepartamentoResidenciaId;

    private Integer codLocalidadResidenciaId;

    private Integer codBarrioResidenciaId;

    @Column(length = 100)
    private String dirCalle;

    private String dirAmpliacion;

    private String dirNroPuerta;

    private String dirNroApartamento;

    private String dirCodigoPostal;

    @Column(length = 5)
    private String dirPiso;
}
