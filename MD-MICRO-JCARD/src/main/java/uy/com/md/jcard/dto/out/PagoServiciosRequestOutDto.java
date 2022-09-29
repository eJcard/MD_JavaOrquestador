package uy.com.md.jcard.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.atos.dto.BaseDto;
import uy.com.md.jcard.dto.in.movimientos.OrdenanteDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagoServiciosRequestOutDto {
    private Integer moneda;
    private Integer monto;
    private OrdenanteDto ordenante;
    private String producto;
    private String rrn;
    private String pinblk;
}
