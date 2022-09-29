package uy.com.md.jcard.dto.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import uy.com.md.jcard.dto.in.movientos_extendidos.DocumentoDto;
import uy.com.md.jcard.dto.in.movimientos.InfoDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteResponseDto {
    // private Boolean success;
    private CustomerResponseDto info;
    private String errors;
    private String trace;
}
