package uy.com.md.sistar.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import uy.com.md.sistar.config.SistarConfig;
import uy.com.md.sistar.dto.LocalidadDTO;
import uy.com.md.sistar.service.RestClientService;

@Service
public class ExternalData {

   private RestClientService restClient;

   private SistarConfig cfg;

   @Autowired
   public void setRestClient(RestClientService restClient) {
      this.restClient = restClient;
   }

   @Autowired
   public void setCfg(SistarConfig cfg) {
      this.cfg = cfg;
   }

   public String getCodLocalidad(Integer idLocalidad, Integer codDepartamento, String codPais) {
      try{
         if(idLocalidad == null || codDepartamento == null){
            return null;
         }
         final String path = String.format("/%d/%d/%s", idLocalidad, codDepartamento, codPais);
         ResponseEntity<LocalidadDTO> result = restClient.getData(cfg.getUrlLocalidades() + path, new ParameterizedTypeReference<LocalidadDTO>() {});
         LocalidadDTO localidadDTO = result.getBody();
         return Optional.of(localidadDTO)
            .map(LocalidadDTO::getRespuesta)
            .map(LocalidadDTO.Respuesta::getCodLocalidadIdSb)
            .map(Object::toString)
            .orElse(null);
      }catch(Exception ex){
         return null;
      }
   }

   public String getCodDepartamento(Integer idDepartamento, String codPais) {
      try{
         if(idDepartamento == null){
            return null;
         }
         final String path = String.format("/%d/%s", idDepartamento, codPais);
         ResponseEntity<LocalidadDTO> result = restClient.getData(cfg.getUrlDepartamentos() + path, new ParameterizedTypeReference<LocalidadDTO>() {});
         LocalidadDTO localidadDTO = result.getBody();
         return Optional.of(localidadDTO)
            .map(LocalidadDTO::getRespuesta)
            .map(LocalidadDTO.Respuesta::getCodDepartamentoIdSb)
            .map(Object::toString)
            .orElse(null);
      }catch(Exception ex){
         return null;
      }
   }
}
