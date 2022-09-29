package uy.com.md.modelo.rcor.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Data
public class Producto implements Serializable {

    private static final long serialVersionUID = 637078694082165594L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id", nullable = false)
    private Integer id;

    @Column(name = "fbaja")
    private LocalDateTime fbaja;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "uact", length = 50)
    private String uact;

    @Column(name = "codigo", length = 100)
    private String codigo;

    private Integer cod_tipo_producto_id;
}
