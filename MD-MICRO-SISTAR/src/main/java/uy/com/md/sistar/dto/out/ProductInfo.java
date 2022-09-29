package uy.com.md.sistar.dto.out;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uy.com.md.sistar.dto.CodeNameTuple;

@Data
public class ProductInfo {
  @Data
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class Card {
    @Getter
    public enum Status {
        ACTIVE("ACTIVE"),
        SUSPENDED("SUSPENDED"),
        STOLEN("STOLEN"),
        LOST("LOST"),
        FRAUD("FRAUD"),
        SUSPENDED_ON_REQUEST("SUSPENDED_ON_REQUEST"),
        SUSPENDED_ACTIVE("SUSPENDED_ACTIVE"),
        CANCELLED("CANCELLED"),
        UNKNOWN(null);

        private final String status;

        @JsonCreator
        Status(String status) {
          this.status = status;
        }

        @JsonValue
        public String getStatus() {
          return this.status;
        }
    }

    private Status estado;
    private Integer codigoBloqueo;
    private String ultimosCuatro;
    private Date expiracion;

    @JsonInclude(Include.NON_NULL)
    private Date fechaSolicitud;
    @JsonIgnore
    String name1;
    @JsonIgnore
    String name2;
    @JsonIgnore
    String lastName1;
    @JsonIgnore
    String lastName2;
  }
  
  @EqualsAndHashCode(callSuper = true)
  @Data
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class Product extends CodeNameTuple {
    String sello;
    String emisor;
    String sucursalEmisora;
    CodeNameTuple grupoAfinidad;
  }

  String cuenta;

  Product producto;

  List<Card> tarjetas;
}
