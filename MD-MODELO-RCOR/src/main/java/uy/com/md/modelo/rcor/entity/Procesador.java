package uy.com.md.modelo.rcor.entity;

import lombok.Data;
import uy.com.md.common.model.Auditable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
public class Procesador extends Auditable implements Serializable {

    private static final long serialVersionUID = 637078694082165784L;

    @Id
    @Column(name = "procesador_id", nullable = false)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String nombre;
}
