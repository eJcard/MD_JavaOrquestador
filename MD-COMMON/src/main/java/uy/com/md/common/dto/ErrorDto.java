package uy.com.md.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {

	@JsonProperty(value="ErrorOn")
	private String errorOn;
    @JsonProperty(value="ErrorType")
    private String errorType;
    @JsonProperty(value="ErrorDescription")
    private String errorDescription;
	
}
