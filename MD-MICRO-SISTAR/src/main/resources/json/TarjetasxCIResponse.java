package uy.com.md.sistar.Schemas.json;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "TarjetasCIBanco")
@Data
public class TarjetasxCIResponse {

   @XmlElement(name = "TarjetasCIBanco")
   protected List<TarjetaCIBanco> tarjetas;

   @Data
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class TarjetaCIBanco {

      private String CodigoError;

      private String MensajeError;

      private String Tarjeta;

      private String Estado_Tarjeta;

      private String Cuenta;

      private String Nombre;

      private String Nombre_2;
      
      private String Apellido_1;
      
      private String Apellido_2;

      private String Codigo_de_Bloqueo;

      private String Vencimiento_AAAAMM;

      private String Tipo_de_Tarjeta;

      private Long Banco_de_la_Tarjeta;

      private Long Codigo_Grupo_Afinidad;

      private String Descripcion_Grupo_Afinidad;

      private Long Codigo_Tipo_Producto;
      
      private String Descripcion_Tipo_Producto;

      private String Tarjeta_Inhabilitada;
   }
}
