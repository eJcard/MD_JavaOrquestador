package uy.com.md.persona.dto.rcor;

import lombok.Data;
import net.atos.dto.BaseDto;

@Data
public class VinculoResponseDto extends BaseDto {
    private Long id;
    private ObtPersonaPorDocumentoRequestDto personaIdA;
    private ObtPersonaPorDocumentoRequestDto personaIdB;
    private CodTipoVinculoDto vinculo;
    private String fechaDesde;
    private String fechaHasta;
}
