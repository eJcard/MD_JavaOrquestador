package uy.com.md.global.dto;

import lombok.Data;
import net.atos.dto.BaseDto;

@Data
public class DocumentoDto extends BaseDto {
    private Integer tipoDoc;
    private String num;
    private Integer pais;
}
