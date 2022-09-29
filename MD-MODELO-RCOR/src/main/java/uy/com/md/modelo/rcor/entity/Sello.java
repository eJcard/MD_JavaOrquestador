package uy.com.md.modelo.rcor.entity;

import lombok.Data;
import uy.com.md.common.model.Auditable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
public class Sello extends Auditable implements Serializable {

    private static final long serialVersionUID = 637078694082165584L;

    @Id
    @Column(name = "sello_id", nullable = false)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nombre;

}
