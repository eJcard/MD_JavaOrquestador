package uy.com.md.modelo.mdw.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import uy.com.md.common.model.Auditable;

import javax.persistence.*;
import java.io.Serializable;

@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
@Entity
@Data
public class Reintento extends Auditable implements Serializable {

    private static final long serialVersionUID = 367078694082165784L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reintento_id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String reqId;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", nullable = false)
    private String request;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private String response;

    private Integer reintentoNumero;
}
