package uy.com.md.sistar.service;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import uy.com.md.sistar.config.SistarConfig;
import uy.com.md.sistar.dto.out.TokenResponse;

@Service
class TokenService {

   private static Date fechaExpiracion;

   private static String token;

   private RestTemplate restTemplate;

   private SistarConfig cfg;

   @Autowired
   public void setCfg(SistarConfig cfg) {
      this.cfg = cfg;
   }

   @Autowired
   public void setRestTemplate(RestTemplate restTemplate) {
      this.restTemplate = restTemplate;
   }

   private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

   String getToken() {
      logger.info("Comienza servicio para obtener token");
      Calendar calendar = Calendar.getInstance();
      if (StringUtils.isBlank(token) || Objects.isNull(fechaExpiracion) || !calendar.getTime().before(fechaExpiracion)) {
         String url = cfg.getTokenUrl();
         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
         headers.add( "Authorization",basicAuth(cfg.getTokenUsername(), cfg.getTokenPassword()));
         MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
         bodyMap.add("grant_type", "client_credentials");
         HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(bodyMap, headers);
         logger.info("Se consulta el servicio: " + url);
         ResponseEntity<TokenResponse> result = restTemplate.exchange(url, HttpMethod.POST, requestEntity, TokenResponse.class);
         if (Objects.nonNull(result.getBody())) {
            token = result.getBody().getAccess_token();
            calendar.add(Calendar.SECOND, result.getBody().getExpires_in());
            fechaExpiracion = calendar.getTime();
            logger.info("Token obtenido, expira: " + fechaExpiracion);
         }
      }
      return token;
   }

   private String basicAuth(String username, String password) {
      String auth = username + ":" + password;
      byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
      return "Basic " + new String(encodedAuth);
   }
}

//HttpHeaders createHeaders(String username, String password){
//   return new HttpHeaders() {{
//         String auth = username + ":" + password;
//         byte[] encodedAuth = Base64.encodeBase64(
//            auth.getBytes(Charset.forName("US-ASCII")) );
//         String authHeader = "Basic " + new String( encodedAuth );
//         set( "Authorization", authHeader );
//      }};
//}