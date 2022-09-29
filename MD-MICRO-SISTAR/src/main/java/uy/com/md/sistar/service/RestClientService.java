package uy.com.md.sistar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import uy.com.md.sistar.controller.AuthorizationInfoBean;

@Service
public class RestClientService {

   private RestTemplate restTemplate;

   private static final Logger logger = LoggerFactory.getLogger(RestClientService.class);

   @Autowired
   private AuthorizationInfoBean authorizationInfo;

   @Autowired
   public void setRestTemplate(RestTemplate restTemplate) {
      this.restTemplate = restTemplate;
   }

   public ResponseEntity getData(String url, ParameterizedTypeReference<?> reference) {
      logger.info("Se consulta el servicio: " + url);
      final String token = authorizationInfo.getToken();
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      headers.setBearerAuth(token);
      HttpEntity<?> requestEntity = new HttpEntity<>(headers);
      final ResponseEntity result = restTemplate.exchange(url, HttpMethod.GET, requestEntity, reference);
      if (!result.getStatusCode().is2xxSuccessful()) {
         throw new IllegalStateException(result.getStatusCode().toString());
      }
      return result;
   }

}
