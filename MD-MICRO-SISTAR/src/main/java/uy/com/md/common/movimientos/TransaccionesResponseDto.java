package uy.com.md.common.movimientos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import uy.com.md.common.movientos_extendidos.DocumentoDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransaccionesResponseDto {
    private Boolean success;
    private InfoDto info;
    private DocumentoDto ordenante;
    private String errors;
    private String trace;
}
