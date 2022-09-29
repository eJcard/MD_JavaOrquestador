package uy.com.md.modelo.mdw.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Immutable
@Table(name = "vw_persona_producto_procesador")
public class VwPersonaProducto implements Serializable {

    private static final long serialVersionUID = 636975006264300901L;

    @Id
    private String codigo;
    private Integer codPaisDocId;
    private Integer codTipoDocId;
    private String numDoc;
    private String numeroCuenta;
    private String nombre;
}
