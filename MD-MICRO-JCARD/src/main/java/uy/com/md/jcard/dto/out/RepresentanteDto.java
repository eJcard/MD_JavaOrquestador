
package uy.com.md.jcard.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepresentanteDto extends BeneficiarioDto{
	private String relacion;
}
