package uy.com.md.global.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.atos.dto.BaseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferenciaRequestDto extends BaseDto {
	private String canal;
    private BigDecimal monto;
    private Integer moneda;
    private String motivo;
    private String referencia;
    private DatosTransferenciaDto ordenante;
    private DatosTransferenciaDto beneficiario;
    private String amdToken;
    private String amdSistema;
    private String concepto;
    private String rrn;
}

