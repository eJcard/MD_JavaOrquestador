package uy.com.md.persona.model.rcor;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import uy.com.md.common.model.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
public class Vinculo extends Auditable implements Serializable {

    private static final long serialVersionUID = 837758694082165784L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vinculo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id_a",nullable = false)
    private Persona personaIdA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id_b", nullable = false)
    private Persona personaIdB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_tipo_vinculo_id", nullable = false)
    private CodTipoVinculo codTipoVinculo;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime fechaDesde;

    private LocalDateTime fechaHasta;

}
