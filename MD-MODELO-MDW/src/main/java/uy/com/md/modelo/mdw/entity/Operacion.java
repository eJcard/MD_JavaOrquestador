package uy.com.md.modelo.mdw.entity;

import lombok.Data;
import uy.com.md.common.model.Auditable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
public class Operacion extends Auditable implements Serializable {

    @Id
    @Column(name = "operacion_id", length = 50)
    private String id;

    private LocalDate fbaja;

    private String controlarColumnas;

    private Integer idServicioCumplimiento;
}
