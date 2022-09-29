package uy.com.md.sistar.dto;

import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import lombok.Data;

@Data
public class LocalidadDTO {

   private List<Object> mensajes;

   @Embedded
   private Respuesta respuesta;

   private Boolean resultado;

   @Embeddable
   @Data
   public class Respuesta {

      private Long codDepartamentoId;

      private Object codDepartamentoIdSb;

      private Object codLocalidadIdSb;

      private Long codPaisId;

      private String corrId;

      private Long id;

      private String nombre;

      private String uact;
   }
}

