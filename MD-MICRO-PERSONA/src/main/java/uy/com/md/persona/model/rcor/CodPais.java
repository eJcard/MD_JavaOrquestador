package uy.com.md.persona.model.rcor;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class CodPais implements Serializable {

    private static final long serialVersionUID = 639074234082165783L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_pais_id")
    private Integer id;

    @Column(length = 2)
    private String alpha2;

    @Column(length = 200)
    private String nombre;

    private Integer codPaisIdMd;
}
