package uy.com.md.common.movimientos;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConceptoDto {
    private String codigo;
    private String descripcion;
}
