package uy.com.md.modelo.rcor.entity;

import lombok.Data;
import uy.com.md.common.model.Auditable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class ProductoTarjetaPrepaga extends Auditable implements Serializable {

    @Id
    @Column(name = "producto_id", nullable = false)
    private Integer productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procesador_id", nullable = false)
    private Procesador procesador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sello_id", nullable = false)
    private Sello sello;

    private String codigoProcesadora;

}
