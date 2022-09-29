package uy.com.md.jcard.dto.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.atos.dto.BaseDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecargaRequestDto extends BaseDto {
    private String canal;
    private String red;
    private String sucursal;
    private BeneficiarioDto beneficiario;
    private String producto;
    private Integer moneda;
    private Double monto;
    private EmpresaDto empresa;
    private String motivo;
    private String comprobante;
    private String rrn;
    private String referencia;
}
