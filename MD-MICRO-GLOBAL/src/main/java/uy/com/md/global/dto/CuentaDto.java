package uy.com.md.global.dto;

import lombok.Data;

@Data
public class CuentaDto {
    private String cuenta;
    private Short sucursal;
    private String caja;
    private String amdToken;
}
