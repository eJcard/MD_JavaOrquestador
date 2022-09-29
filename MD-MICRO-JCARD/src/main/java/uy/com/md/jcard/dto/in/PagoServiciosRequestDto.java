package uy.com.md.jcard.dto.in;

import lombok.Data;
import net.atos.dto.BaseDto;
import uy.com.md.jcard.dto.in.movimientos.OrdenanteDto;

@Data
public class PagoServiciosRequestDto extends BaseDto {
    private Integer moneda;
    private Integer monto;
    private OrdenanteDto ordenante;
    private String producto;
    private String rrn;
    private String pinblk;
}
