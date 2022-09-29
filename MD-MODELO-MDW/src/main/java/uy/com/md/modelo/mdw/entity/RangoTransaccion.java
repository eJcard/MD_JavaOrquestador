package uy.com.md.modelo.mdw.entity;

import lombok.Data;
import uy.com.md.common.model.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
public class RangoTransaccion extends Auditable implements Serializable {

    private static final long serialVersionUID = 637078694082165784L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rango_transaccion_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long agenteExternoId;

    @Column(nullable = false)
    private Integer codMonedaId;

    @Column(nullable = false)
    private BigDecimal minimo;

    @Column(nullable = false)
    private BigDecimal maximo;
}
