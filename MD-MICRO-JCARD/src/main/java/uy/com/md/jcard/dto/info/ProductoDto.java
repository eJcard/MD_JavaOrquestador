package uy.com.md.jcard.dto.info;

import lombok.Data;

@Data
public class ProductoDto {
    private String codigo;
    private String nombre;
    private String sello;
    private GrupoAfinidadDto grupoAfinidad;
}
