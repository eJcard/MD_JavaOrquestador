package uy.com.md.sistar.Schemas.json.out;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import uy.com.md.sistar.dto.out.ProductInfo.Card;
import uy.com.md.sistar.dto.out.ProductInfo.Product;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountInfoDto {
  @Data
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class ClientInfo {
    String nombre;
    String apellido;
    String apellido2;
    DocDto doc;
    String actividad;
    String email;
    Date fechaDeNacimiento;
    String sexo;
    String estadoCivil;
    Boolean operaPorTerceros;
    String nacionalidad;
    List<TelefonoDto> telefonos;
    DireccionDto direccion;
  }
  ClientInfo cliente;
  Product producto;
  Card tarjeta;
  @JsonIgnore
  String emisor;
  @JsonIgnore
  String gaf;
}
