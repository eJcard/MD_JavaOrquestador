package uy.com.md.jcard.dto.in;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferenciaRequestDto {
    private String canal;
    private BigDecimal monto;
    private Integer moneda;
    private DatosTransferenciaDto ordenante;
    private DatosTransferenciaDto beneficiario;
    private String motivo;
    private String comprobante;
    private String referencia;
    private String concepto;
    private String rrn;
}