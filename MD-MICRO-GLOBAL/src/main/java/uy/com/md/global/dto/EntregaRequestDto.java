package uy.com.md.global.dto;

import lombok.Data;
import net.atos.dto.BaseDto;

@Data
public class EntregaRequestDto extends BaseDto {
    private String trackid;
    private Integer tipodocumento;
    private Integer documento;
    private Short sucursal;
    private String caja;
}
