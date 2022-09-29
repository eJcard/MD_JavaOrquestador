
package uy.com.md.jcard.dto.out;


import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import net.atos.dto.BaseDto;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardsRequestDto{
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
