package uy.com.md.modelo.mdw.entity;

import lombok.Data;
import uy.com.md.common.model.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
public class PermisoRedOperacion extends Auditable implements Serializable {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permiso_red_operacion_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "red_id", nullable = false)
    private Red red;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operacion_id", nullable = false)
    private Operacion operacion;

    private LocalDateTime fbaja;
    
    @Column(name = "id_servicio_cumplimiento")
    private Integer idServicioCumplimiento;
}
