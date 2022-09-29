package uy.com.md.jcard.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import uy.com.md.jcard.enumerated.TipoEntrega;
import net.atos.dto.BaseDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntregaRequestDto {
    private String usuarioId;
    private String sucursal; 
    private TipoEntrega tipoEntrega;    
    private String red;
}
