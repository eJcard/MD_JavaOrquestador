package uy.com.md.jcard.dto.in;

import lombok.Data;
import net.atos.dto.BaseDto;
import uy.com.md.jcard.dto.out.BeneficiarioDto;
import uy.com.md.jcard.dto.out.RepresentanteDto;

@Data
public class SolicitudRequestDto{
    private BeneficiarioDto beneficiario;
    private String sucursal;
    private String producto;
    private String red;
    private String grupoAfinidad;
    private RepresentanteDto representante;
    private String emisor;
    private Boolean operaPorTerceros;
    private String sucursalSolicitud;
    private String redSolicitud;
}
