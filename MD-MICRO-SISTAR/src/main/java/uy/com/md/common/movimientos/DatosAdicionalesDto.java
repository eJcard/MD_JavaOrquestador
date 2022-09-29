package uy.com.md.common.movimientos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatosAdicionalesDto {
    private String canal;
    private OrdenanteDto ordenante;
    private BeneficiarioDto beneficiario;
    private String motivo;
    private String referencia;
    private Long comprobante;
}
