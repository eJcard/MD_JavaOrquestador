package uy.com.md.common.movimientos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import uy.com.md.common.movientos_extendidos.InfoExtendidaDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransaccionesDto {
    private String codigo;
    private String estado;
    private String fechaHora;
    private String fechaAplicacion;
    private String fechaPresentacion;
    private String rrn;
    private String rrnReferencia;
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
    private String observaciones;
    private Double movimientoConversionMD;
    private String mcc;
    private ConceptoDto concepto;
    private String comercio;
    private String comercioDescripcion;
    private String rubro;
    private DatosAdicionalesDto datosAdicionales;
    private Integer reversalCount;
    private Integer voidCount;
    private Integer completionCount;
    private InfoExtendidaDto informacionExtendida;
    private Short sbCode;
    private Short sbSubcode;
    private Short sbStatusType;
    private Short sbStatusCode;
    private String requestExternalId;
    private String sbInfinitusCode;
}
