
package uy.com.md.jcard.dto.out;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.atos.dto.BaseDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelefonoDto{
	private Integer tipoTel;
	private String num;
}
