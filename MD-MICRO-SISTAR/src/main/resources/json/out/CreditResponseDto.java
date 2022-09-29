package uy.com.md.sistar.Schemas.json.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditResponseDto {
	private Boolean success;

	@Schema(example = "123")
	private String comprobante;

	@Schema(example = "10095807490")
	private String cuenta;

	@Schema(example = "810161")
	private String codigoAutorizacion;

	@Schema(example = "200.21")
	private String itc;
}
