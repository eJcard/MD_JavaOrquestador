
package uy.com.md.jcard.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.atos.dto.BaseDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardsResponseDto {
	private Boolean success;
	private String realAccount;
	private String trace;
	private String errors;
	private String codigoAutorizacion;
	private String itc;
	private String codigoResultado;
}
