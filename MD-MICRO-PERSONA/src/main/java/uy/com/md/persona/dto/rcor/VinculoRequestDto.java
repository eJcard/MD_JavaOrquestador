package uy.com.md.persona.dto.rcor;

import lombok.Data;
import net.atos.dto.BaseDto;

import java.time.LocalDateTime;

@Data
public class VinculoRequestDto extends BaseDto {
    private Long id;
    private ObtPersonaPorDocumentoRequestDto personaIdA;
    private ObtPersonaPorDocumentoRequestDto personaIdB;
    private String vinculo;
    private String fechaDesde;
    private String fechaHasta;
    private String fact;
}
