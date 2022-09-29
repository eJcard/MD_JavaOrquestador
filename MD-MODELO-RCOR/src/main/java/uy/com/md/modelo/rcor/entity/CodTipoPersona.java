package uy.com.md.modelo.rcor.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class CodTipoPersona implements Serializable {
	
	private static final long serialVersionUID = 637758694082165784L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_tipo_persona_id")
    private Integer id;
    
    @Column (length = 100)
    private String descripcion;
}
