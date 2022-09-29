package uy.com.md.jcard.dto.in.movimientos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JcardMovimientosParamsDto {
    private String start;
    private String end;
    private Integer offset;
    private Integer length;
    private String itc;
    private String order;
}
