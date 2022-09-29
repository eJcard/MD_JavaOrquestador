package uy.com.md.modelo.mdw.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import uy.com.md.common.model.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
@Entity
@Data
public class Idempotencia extends Auditable implements Serializable {

    private static final long serialVersionUID = 637078694082165784L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idempotencia_id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String reqId;

    @Column(nullable = false, unique = true)
    private String slug;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", nullable = false)
    private String request;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private String response;

    private String jcard;

    private String global;

    private String sistarbanc;

    private String operacionId;

    @Column(length = 20, nullable = false)
    private String estado;

    private LocalDateTime fbaja;
}
