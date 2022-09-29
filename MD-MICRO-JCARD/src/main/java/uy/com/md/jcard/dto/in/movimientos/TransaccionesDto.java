package uy.com.md.jcard.dto.in.movimientos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import uy.com.md.jcard.dto.in.movientos_extendidos.InfoExtendidaDto;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransaccionesDto {
    private String codigo;
    private String estado;
    private String fechaHora;
    private String fechaAplicacion;
    private String fechaPresentacion;
    private String rrn;
    private String fechaUltimoEstado;
    private BigDecimal monto;
    private BigDecimal montoOrigen;
    private String monedaOrigen;
    private String cupon;
    private String codigoAprobacion;
    private String moneda;
    private String irc;
    private String canal;
    private String descripcion;
    private Double movimientoConversionMD;
    private String mcc;
    private ConceptoDto concepto;
    private String comercio;
    private String rubro;
    private DatosAdicionalesDto datosAdicionales;
    private Integer reversalCount;
    private InfoExtendidaDto informacionExtendida;
}
