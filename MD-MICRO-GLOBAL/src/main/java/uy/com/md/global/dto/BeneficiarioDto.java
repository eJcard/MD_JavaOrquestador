package uy.com.md.global.dto;

import lombok.Data;
import net.atos.dto.BaseDto;

@Data
public class BeneficiarioDto extends BaseDto {
    private Integer tipoDoc;
    private Integer pais;
    private String doc;
}
