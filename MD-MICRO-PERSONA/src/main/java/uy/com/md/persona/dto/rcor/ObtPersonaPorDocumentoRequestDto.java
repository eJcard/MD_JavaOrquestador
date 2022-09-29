package uy.com.md.persona.dto.rcor;

import lombok.Data;
import net.atos.dto.BaseDto;

@Data
public class ObtPersonaPorDocumentoRequestDto extends BaseDto {
    private Integer paisDoc;
    private Integer tipoDoc;
    private String numDoc;
    private String origen;
}
