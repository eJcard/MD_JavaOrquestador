package uy.com.md.jcard.dto.in.movimientos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdenanteDto {
    private String tipoDoc;
    private String pais;
    private String doc;
    private String nombre;
    private String cuenta;
}
