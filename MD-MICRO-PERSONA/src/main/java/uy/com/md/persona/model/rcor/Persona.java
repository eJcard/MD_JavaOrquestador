package uy.com.md.persona.model.rcor;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import uy.com.md.common.model.Auditable;

@Entity
@DynamicUpdate
@Data
public class Persona extends Auditable implements Serializable {
    private static final long serialVersionUID = 637074234082165784L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "persona_id")
    private Integer id;

    private Integer codPaisDocId;

    private Integer codTipoDocId;

    @Column(length = 100)
    private String numDoc;

    @Column(nullable = false)
    private String denominacion;

    @Column(nullable = false, columnDefinition="tinyint(1) default 1")
    private Boolean personaActiva;

    @Column(length = 100)
    private String ucreacion;

    @CreationTimestamp
    private LocalDateTime fcreacion;

    private Integer codTipoPersonaId;
}
