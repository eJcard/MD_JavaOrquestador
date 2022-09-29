package uy.com.md.modelo.rcor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import uy.com.md.common.model.Auditable;

@Data
@Entity
public class GrupoAfinidad extends Auditable implements Serializable {

    private static final long serialVersionUID = 637758694082165784L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grupo_afinidad_id")
    private Integer id;

    @Column(length = 100)
    private String nombre;

}
