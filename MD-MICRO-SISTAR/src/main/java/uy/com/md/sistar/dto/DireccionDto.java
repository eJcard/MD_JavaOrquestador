package uy.com.md.sistar.dto;

import lombok.Data;

@Data
public class DireccionDto {

   private Boolean residente;

   private String calle;

   private String puerta;

   private String apto;

   private String codigoPostal;

   private Integer departamento;

   private Integer localidad;

   private Integer pais;

   private String piso;
}
