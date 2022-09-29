package uy.com.md.jcard.dto.info;

import lombok.Data;

@Data
public class DireccionDto {
    private Boolean residente;
    private String calle;
    private String puerta;
    private String codigoPostal;
    private String departamento;
    private String localidad;
    private String pais;
    private String apto;
    private String piso;
    private String barrio;
    private String ampliacionCalle;
}
