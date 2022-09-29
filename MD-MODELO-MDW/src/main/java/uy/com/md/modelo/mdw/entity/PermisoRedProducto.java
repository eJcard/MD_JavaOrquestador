package uy.com.md.modelo.mdw.entity;

import lombok.Data;
import uy.com.md.common.model.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
public class PermisoRedProducto extends Auditable implements Serializable {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permiso_red_producto_id", nullable = false)
    private Long id;

    @Column(name="codigo_producto", length = 10, nullable = false)
    private String codigoProducto;

    @Column(name="codigo_grupo_afinidad", length = 10, nullable = true)
    private String codigoGrupoAfinidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "red_id", nullable = false)
    private Red red;

    @Column(nullable = true)
    private LocalDateTime fbaja;



}
