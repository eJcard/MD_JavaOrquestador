package uy.com.md.sistar.Schemas.json;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.util.List;

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

