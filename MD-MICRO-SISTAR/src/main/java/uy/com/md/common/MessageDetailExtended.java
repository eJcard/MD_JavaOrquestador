package uy.com.md.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uy.com.md.mensajes.dto.MensajeDetail;
import uy.com.md.mensajes.enumerated.MensajeType;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDetailExtended extends MensajeDetail {
  @JsonProperty("trace")
  private String trace;

  public MessageDetailExtended(String origen, String codigo, String mensaje, MensajeType tipo, String trace){
    super(origen, codigo, mensaje, tipo);
    this.trace = trace;
  }
}
