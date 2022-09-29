package uy.com.md.sistar;

import io.swagger.v3.oas.models.examples.Example;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;
import uy.com.md.sistar.config.SistarConfig;
import uy.com.md.sistar.filter.SchemaValidationFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

@SpringBootApplication
@ComponentScan({ "uy.com.md", "net.atos.external.config" })
public class MdSistarApplication {

   @Value("classpath:/examples/get-products.json")
   Resource successfulGetProductsExampleResource;

   static {
      //TODO: for localhost testing only
      javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> true);
   }

   @Autowired
   public void setCfg(SistarConfig cfg) {
   }

   public static void main(String[] args) {
      SpringApplication.run(MdSistarApplication.class, args);
   }

   public static String asString(Resource resource) {
      try (Reader reader = new InputStreamReader(resource.getInputStream())) {
         return FileCopyUtils.copyToString(reader);
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   @Bean
   public OpenApiCustomiser openApiCustomiser(Collection<Entry<String, Example>> examples) {
      return openAPI -> examples.forEach(example -> openAPI.getComponents().addExamples(example.getKey(), example.getValue()));
   }

   @Bean
   public Map.Entry<String, Example> successfulGetProductsExample() {
      Example successfulGetProductsExample = new Example();
      Map.Entry<String, Example> entry = new AbstractMap.SimpleEntry<>("successfulGetProductsExample", successfulGetProductsExample);
      successfulGetProductsExample.setSummary("Successful operation");
      successfulGetProductsExample.setDescription("Successful operation");
      Map<String, Object> parsed = new GsonJsonParser().parseMap(asString(successfulGetProductsExampleResource));
      successfulGetProductsExample.setValue(parsed);
      return entry;
   }

   @Bean
   public RestTemplate restTemplate() {
      CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
      HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
      requestFactory.setHttpClient(httpClient);
      return new RestTemplate(requestFactory);
   }

   @Order(Ordered.HIGHEST_PRECEDENCE)
   @Bean
   public FilterRegistrationBean<SchemaValidationFilter> schemaValidationFilter() {
      FilterRegistrationBean<SchemaValidationFilter> registrationBean = new FilterRegistrationBean<>();
      registrationBean.setFilter(new SchemaValidationFilter());
      registrationBean.addUrlPatterns("*");
      return registrationBean;
   }
}
