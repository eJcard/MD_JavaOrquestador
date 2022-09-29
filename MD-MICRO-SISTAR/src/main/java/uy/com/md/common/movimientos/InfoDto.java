package uy.com.md.common.movimientos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InfoDto {
    private String journalCode;
    private List<TransaccionesDto> transactions;
    private PaginadoDto paginado;
}
