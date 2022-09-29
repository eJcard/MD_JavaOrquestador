package uy.com.md.global.dto;

import lombok.Data;
import net.atos.dto.BaseDto;

@Data
public class SolcitudRequestDto extends BaseDto {
    private PersonaDto beneficiario;
    private Integer producto;
    private Integer grupoAfinidad;
    private Integer red;
    private Short sucursal;
    private Integer emisor;
    private PersonaDto representante;
    private Short sucursalOrigen;
    private String caja;
}
