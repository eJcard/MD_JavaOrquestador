package uy.com.md.modelo.mdw.entity;

import lombok.Data;
import uy.com.md.common.model.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
public class Red extends Auditable implements Serializable {

    private static final long serialVersionUID = 637078694282165784L;

    @Id
    @Column(name = "red_id", nullable = false)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nombre;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agente_externo_id", nullable = false)
    private AgenteExterno agenteExterno;

    @Column
    private LocalDateTime fbaja;

}
