package uy.com.md.modelo.rcor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class CodMoneda implements Serializable {

	private static final long serialVersionUID = 637369746150633164L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cod_moneda_id", nullable = false)
	private Long codMonedaId;

	@Column(name = "nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "simbolo", length = 5, nullable = false)
	private String simbolo;

}
