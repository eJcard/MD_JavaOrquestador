package uy.com.md.sistar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class SistarConfig {

   private String tokenUrl;

   private String tokenUsername;

   private String tokenPassword;

   private String urlLocalidades;

   private String urlDepartamentos;

   @Value("${spring.profiles.active}")
   String activeProfile;

   @Value("${extmap.url.admin}")
   public void setTokenUrl(String tokenUrl) {
      this.tokenUrl = tokenUrl;
   }

   @Value("${extmap.token.username}")
   public void setTokenUsername(String tokenUsername) {
      this.tokenUsername = tokenUsername;
   }

   @Value("${extmap.token.password}")
   public void setTokenPassword(String tokenPassword) {
      this.tokenPassword = tokenPassword;
   }

   @Value("${extmap.url.localidades}")
   public void setUrlLocalidades(String urlLocalidades) {
      this.urlLocalidades = urlLocalidades;
   }

   @Value("${extmap.url.departamentos}")
   public void setUrlDepartamentos(String urlDepartamentos) {
      this.urlDepartamentos = urlDepartamentos;
   }
}
