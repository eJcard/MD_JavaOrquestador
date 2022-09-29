package uy.com.md.sistar.Schemas.json.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionFraude {

    private Long id;

    @JsonProperty("moneda")
    private Long currency;

    @JsonProperty("inicio")
    private String from;

    @JsonProperty("fin")
    private String to;


}
