package uy.com.md.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import uy.com.md.global.soap.GlobalClient;

@Configuration
public class GlobalSolcitudClientConfig {

    @Value("${global.solicitud.soap.uri}")
    public String globalSolicitudSoapUri;

    @Bean
    public Jaxb2Marshaller marshallerSolicitud() {
        Jaxb2Marshaller marshallerSolicitud = new Jaxb2Marshaller();
        marshallerSolicitud.setContextPath("uy.com.md.global.soap.client.solicitudproductonominado");
        return marshallerSolicitud;
    }

    @Bean
    public GlobalClient globalSolicitudClient(Jaxb2Marshaller marshallerSolicitud) {
        GlobalClient client = new GlobalClient();
        client.setDefaultUri(globalSolicitudSoapUri);
        client.setMarshaller(marshallerSolicitud);
        client.setUnmarshaller(marshallerSolicitud);
        return client;
    }
}
