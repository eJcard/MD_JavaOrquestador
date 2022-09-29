package uy.com.md.sistar.Schemas.json.in;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CardStatusUpdateDto {
  @JsonProperty("estado")
  @Schema(allowableValues = {"ACTIVE", "SUSPENDED"}, required = true)
  String status;
  
  @JsonIgnore
  String account;
}
