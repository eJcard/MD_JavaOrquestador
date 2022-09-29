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
public class Contacto extends Auditable implements Serializable {

    private static final long serialVersionUID = 639074234082165784L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contacto_id")
    private Integer id;

    @Column(name = "persona_id", nullable = false)
    private Integer personaId;

    @Column(name = "cod_tipo_contacto_id", nullable = false)
    private Integer codTipoContactoId;

    @Column(length = 100)
    private String etiqueta;

    @Column(length = 200)
    private String contacto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origen_id")
    private Origen origen;
}
