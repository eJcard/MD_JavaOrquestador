package uy.com.md.sistar.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CardStatusUpdateDto {
  @JsonProperty("estado")
  @Schema(allowableValues = {"ACTIVE", "SUSPENDED"}, required = true)
  String status;
  
  @JsonIgnore
  String account;
}
