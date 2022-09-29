package uy.com.md.global.dto;

import lombok.Data;
import net.atos.dto.BaseDto;

@Data
public class EmpresaDto extends BaseDto {
    private Integer tipoDoc;
    private Integer pais;
    private String doc;
    private String nombre;
}
