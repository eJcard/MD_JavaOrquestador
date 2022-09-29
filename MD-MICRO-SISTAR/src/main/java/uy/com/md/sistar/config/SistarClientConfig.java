package uy.com.md.sistar.config;

import org.apache.http.HttpHost;
import org.apache.http.client.AuthCache;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import uy.com.md.sistar.service.SistarServiceProperties;
import uy.com.md.sistar.soap.SistarClient;

import java.net.URI;
import java.util.Collections;

@Configuration
@RefreshScope
public class SistarClientConfig {

   @Autowired
   SistarServiceProperties config;

   @Value("${sistar.soap.uri}")
   public String sistarSoapMDF2Uri;

   @Value("${sistar.soap.uri4}")
   public String sistarSoapMDF25Uri;

   @Value("${sistar.soap.uri2}")
   public String sistarSoapWSMidineroUri;

   @Value("${sistar.soap.uri3}")
   public String sistarSoapTrfC2BUri;

   @Value("${sistar.soap.uri_v2}")
   public String sistarSoapMDV2Uri;

   @Value("${sistar.soap.uri5}")
   public String sistarSoapUpdCardStatus;

   @Bean
   public Jaxb2Marshaller marshaller() {
      Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
      marshaller.setContextPath("uy.com.md.sistar.soap.client");
      marshaller.setMarshallerProperties(Collections.singletonMap(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"));
      return marshaller;
   }

   @Bean
   public Jaxb2Marshaller customMarshaller() {
      Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
      marshaller.setContextPath("uy.com.md.sistar.dto");
      marshaller.setMarshallerProperties(Collections.singletonMap(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"));
      return marshaller;
   }

   @Bean
   public Jaxb2Marshaller c2bTransfersMarshaller() {
      Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
      marshaller.setContextPath("uy.com.md.sistar.soap.client2");
      marshaller.setMarshallerProperties(Collections.singletonMap(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"));
      return marshaller;
   }

   @Bean(name = "SistarClientMDF2")
   public SistarClient sistarClient(Jaxb2Marshaller marshaller, HttpComponentsMessageSender  httpComponentsMessageSender) {
      SistarClient client = new SistarClient();
      client.setDefaultUri(sistarSoapMDF2Uri);
      client.setMarshaller(marshaller);
      client.setUnmarshaller(marshaller);
      client.setMessageSender(httpComponentsMessageSender);
      return client;
   }

   @Bean(name = "SistarClientMDF25")
   public SistarClient SsstarClientMDF25(Jaxb2Marshaller marshaller, HttpComponentsMessageSender  httpComponentsMessageSender) {
      SistarClient client = new SistarClient();
      client.setDefaultUri(sistarSoapMDF25Uri);
      client.setMarshaller(marshaller);
      client.setUnmarshaller(marshaller);
      client.setMessageSender(httpComponentsMessageSender);
      return client;
   }

   @Bean(name = "SistarClientWSMidinero")
   public SistarClient sistarTransactionClient(Jaxb2Marshaller marshaller, HttpComponentsMessageSender  httpComponentsMessageSender) {
      SistarClient client = new SistarClient();
      client.setDefaultUri(sistarSoapWSMidineroUri);
      client.setMarshaller(marshaller);
      client.setUnmarshaller(marshaller);
      client.setMessageSender(httpComponentsMessageSender);
      return client;
   }

   @Bean(name = "SistarClientC2BMultiproc")
   public SistarClient sistarC2BMultiprocClient(Jaxb2Marshaller c2bTransfersMarshaller, HttpComponentsMessageSender  httpComponentsMessageSender) {
      SistarClient client = new SistarClient();
      client.setDefaultUri(sistarSoapTrfC2BUri);
      client.setMarshaller(c2bTransfersMarshaller);
      client.setUnmarshaller(c2bTransfersMarshaller);
      client.setMessageSender(httpComponentsMessageSender);
      return client;
   }

   @Bean(name = "SistarClientUpdCardStatus")
   public SistarClient sistarClientUpdCardStatusClient(Jaxb2Marshaller marshaller, HttpComponentsMessageSender  httpComponentsMessageSender) {
      SistarClient client = new SistarClient();
      client.setDefaultUri(sistarSoapUpdCardStatus);
      client.setMarshaller(marshaller);
      client.setUnmarshaller(marshaller);
      client.setMessageSender(httpComponentsMessageSender);
      return client;
   }

   @Bean(name = "SistarClientWSMDCustom")
   public SistarClient sistarClient3(Jaxb2Marshaller customMarshaller, Jaxb2Marshaller marshaller, HttpComponentsMessageSender  httpComponentsMessageSender) {
      SistarClient client = new SistarClient();
      client.setDefaultUri(sistarSoapWSMidineroUri);
      client.setMarshaller(marshaller);
      client.setUnmarshaller(customMarshaller);
      client.setMessageSender(httpComponentsMessageSender);
      return client;
   }

   @Bean(name = "SistarClientMDV2")
   public SistarClient sistarClientV2(Jaxb2Marshaller marshaller, HttpComponentsMessageSender  httpComponentsMessageSender) {
      SistarClient client = new SistarClient();
      client.setDefaultUri(sistarSoapMDV2Uri);
      client.setMarshaller(marshaller);
      client.setUnmarshaller(marshaller);
      client.setMessageSender(httpComponentsMessageSender);
      return client;
   }
   @Bean
   public HttpComponentsMessageSender httpComponentsMessageSender() {
      HttpComponentsMessageSender sender = new HttpComponentsMessageSender();
      sender.setReadTimeout(config.getForce_read_timeout());
      sender.setConnectionTimeout(config.getForce_connect_timeout());
      return sender;
   }
   @Bean
   public WebServiceMessageSender customSender() {
      HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
      httpComponentsMessageSender.setConnectionTimeout(config.getForce_connect_timeout());
      httpComponentsMessageSender.setReadTimeout(config.getForce_read_timeout());
      return new HttpComponentsMessageSender() {
         @Override
         protected HttpContext createContext(URI uri) {
            HttpHost target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            AuthCache authCache = new BasicAuthCache();
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(target, basicAuth);
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);
            return localContext;
         }
      };
   }
}