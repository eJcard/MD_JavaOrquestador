package uy.com.md.persona.model.rcor;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class CodTipoVinculo implements Serializable {

    private static final long serialVersionUID = 937758694082165784L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_tipo_vinculo_id")
    private Integer id;

    private String nombre;
}
