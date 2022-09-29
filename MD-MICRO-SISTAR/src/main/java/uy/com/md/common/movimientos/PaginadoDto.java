package uy.com.md.common.movimientos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginadoDto {

    private int cantidadPaginas;
    private int cantidadRegistros;
    private int paginaActual;
    private int tamanioPagina;

}