package uy.com.md.persona.dto.rcor;

import lombok.Data;
import net.atos.dto.BaseDto;
import uy.com.md.persona.dto.rcor.ObtPersonaPorDocumentoRequestDto;

@Data
public class VinculosDto extends BaseDto {
    private Long id;
    private ObtPersonaPorDocumentoRequestDto personaIdA;
    private String vinculo;
    private String fechaDesde;
    private String fechaHasta;
}
