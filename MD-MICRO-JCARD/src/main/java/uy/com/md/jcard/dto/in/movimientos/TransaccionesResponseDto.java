package uy.com.md.jcard.dto.in.movimientos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import uy.com.md.jcard.dto.in.movientos_extendidos.DocumentoDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransaccionesResponseDto {
    private Boolean success;
    private InfoDto info;
    private DocumentoDto ordenante;
    private String errors;
    private String trace;
}
