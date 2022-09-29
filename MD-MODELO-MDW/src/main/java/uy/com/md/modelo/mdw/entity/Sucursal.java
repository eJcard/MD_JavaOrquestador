package uy.com.md.modelo.mdw.entity;

import lombok.Data;
import uy.com.md.common.model.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
public class Sucursal extends Auditable implements Serializable {

    private static final long serialVersionUID = 637978694082165784L;

    @Id
    @Column(name = "sucursal_id", nullable = false)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nombre;

    @Column(length = 10, nullable = false)
    private String codigo;

    @Column(nullable = true)
    private LocalDateTime fbaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "red_id", nullable = false)
    private Red red;

    @Column(length = 10)
    private String codigo_global;

    @Column(length = 10)
    private String codigo_jcard;

    @Column(length = 10)
    private String codigo_sistarbanc;
}
