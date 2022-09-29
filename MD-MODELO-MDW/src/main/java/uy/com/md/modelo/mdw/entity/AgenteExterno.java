package uy.com.md.modelo.mdw.entity;

import lombok.Data;
import uy.com.md.common.model.Auditable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
public class AgenteExterno extends Auditable implements Serializable {

    private static final long serialVersionUID = 637778694382165784L;

    @Id
    @Column(name = "agente_externo_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Integer codPaisDocId;

    @Column(nullable = false)
    private Integer codTipoDocId;

    @Column(length = 100, nullable = false)
    private String numDoc;

    @Column(length = 100, nullable = false)
    private String nombreAplicacion;

    @Column(length = 100, nullable = false)
    private String clientId;
}
