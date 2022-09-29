package uy.com.md.persona.model.rcor;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class CodTipoDoc implements Serializable {

    private static final long serialVersionUID = 639074234082165782L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_tipo_doc_id")
    private Integer id;

    @Column(length = 200)
    private String descripcion;

    @Column(length = 200)
    private String alpha;

    private Integer codTipoDocIdMd;
}
