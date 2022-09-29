package uy.com.md.jcard.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.atos.dto.BaseDto;
import uy.com.md.jcard.dto.in.BeneficiarioDto;
import uy.com.md.jcard.dto.in.EmpresaDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecargaRequestOutDto {
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
