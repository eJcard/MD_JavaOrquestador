package uy.com.md.sistar.Schemas.json.in;

import lombok.Data;

@Data
public class ReemplazoRequestDto {

   private String pais;

   private String tipoDocumento;

   private String documento;

   private String nroSobre;

   private Long cuenta;

   private Long emisor;

   private String producto;
}
