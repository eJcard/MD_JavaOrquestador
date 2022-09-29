package uy.com.md.sistar.dto;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory extends uy.com.md.sistar.soap.client.ObjectFactory {

   public TarjetasxCIResponse createTarjetasxCICustomResponse() {
      return new TarjetasxCIResponse();
   }
}
