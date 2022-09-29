package uy.com.md.global.dto;

import lombok.Data;
import net.atos.dto.BaseDto;
import uy.com.md.global.soap.client.aamdsoapautorizarpago.AmdAutorizarPagoEntrada;

import java.math.BigDecimal;

@Data
public class AutorizarPagoRequestDto extends BaseDto {
   
    public Short documentoTipo;
    public String documentoNumero;
    public Short productoTipo;
    public String pin;
    public Short moneda;
    public BigDecimal importe;
    public BigDecimal descuentoIva;
    public AmdAutorizarPagoEntrada.Metadata metadata;
    private String amdSistema;
    private String amdRequestId;
    private String subAgencia;
    private Short sucursal;
    private String caja;
    private String usuario;

}
