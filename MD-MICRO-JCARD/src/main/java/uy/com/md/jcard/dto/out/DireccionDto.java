
package uy.com.md.jcard.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DireccionDto{
    private String calle;
    private String puerta;
    private String apto;
    private Integer departamento;
    private Integer localidad;
    private String piso;
    private Boolean residente;
    private String codigoPostal;
    private String pais;
    private String barrio;
    private String ampliacionCalle;
}
