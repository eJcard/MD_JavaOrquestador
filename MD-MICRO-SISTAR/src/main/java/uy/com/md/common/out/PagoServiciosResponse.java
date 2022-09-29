package uy.com.md.common.out;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagoServiciosResponse {
    Integer codigoAutorizacion;
    Long codigoResultado;
    Boolean success;
    String itc;
    int rechaMotiCodi;
    String rechaMotiDescri;
}
