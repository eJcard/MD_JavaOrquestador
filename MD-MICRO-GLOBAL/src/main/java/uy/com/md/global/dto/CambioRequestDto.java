package uy.com.md.global.dto;

import lombok.Data;
import net.atos.dto.BaseDto;
import uy.com.md.common.dto.SdtCamposValorDto;

import java.math.BigDecimal;

@Data
public class CambioRequestDto extends BaseDto {
    protected Long cuentanumero;
    protected Short moneda;
    protected BigDecimal monto;
    protected Integer origen;
    protected String observaciones;
    protected long nrocomprobante;
    protected SdtCamposValorDto sdtcamposvalor;
    private String amdCliente;
}
