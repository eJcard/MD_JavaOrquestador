package uy.com.md.common.in;

import java.math.BigDecimal;

import net.atos.dto.BaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uy.com.md.common.movimientos.OrdenanteDto;

@EqualsAndHashCode(callSuper = true)
@Data
public class PagoServiciosRequestDto extends BaseDto {
    private Integer moneda;
    private BigDecimal monto;
    private BigDecimal montoIva;
    private OrdenanteDto ordenante;
    private String producto;
    private String rrn;
    private String pinblk;
    private Integer marca;
    private Integer emisor;
    private Integer ref;
    private Boolean isVoid = false;
}