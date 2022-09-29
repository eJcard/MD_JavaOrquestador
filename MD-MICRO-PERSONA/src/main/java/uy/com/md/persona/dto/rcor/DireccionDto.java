package uy.com.md.persona.dto.rcor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.atos.dto.BaseDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DireccionDto extends BaseDto {
    private Long id;
    private Integer personaId;
    private String origenId;
    private Integer codPaisResidenciaId;
    private Integer codDepartamentoResidenciaId;
    private Integer codLocalidadResidenciaId;
    private Integer codBarrioResidenciaId;
    private String dirCalle;
    private String dirAmpliacion;
    private String dirNroPuerta;
    private String dirNroApartamento;
    private String dirCodigoPostal;
    private String dirPiso;
}
