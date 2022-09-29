package uy.com.md.common.movimientos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BeneficiarioDto {
    private String nombre;
    private BancoDto banco;
}
