package uy.com.md.global.dto;

import lombok.Data;
import net.atos.dto.BaseDto;

@Data
public class DireccionDto extends BaseDto {
    private Boolean residente;
    private String calle;
    private String puerta;
    private String apto;
    private String codigoPostal;
    private Integer departamento;
    private Integer localidad;
    private Integer pais;
    private String piso;
    private Integer barrio;
    private String ampliacionCalle;
}
