package uy.com.md.global.dto;

import lombok.Data;
import net.atos.dto.BaseDto;

@Data
public class RecargaRequestDto extends BaseDto {
    private String canal;
    private Integer red;
    private Short sucursal;
    private BeneficiarioDto beneficiario;
    private Integer producto;
    private Short moneda;
    private Double monto;
    private EmpresaDto empresa;
    private String motivo;
    private String comprobante;
    private String rrn;
    private String referencia;
    private String caja;
}
