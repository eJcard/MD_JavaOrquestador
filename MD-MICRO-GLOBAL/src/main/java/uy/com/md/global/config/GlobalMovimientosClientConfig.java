package uy.com.md.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import uy.com.md.global.soap.GlobalClient;

@Configuration
public class GlobalMovimientosClientConfig {

    @Value("${global.movimientos.soap.uri}")
    public String globalMovimientosSoapUri;

    @Bean
    public Jaxb2Marshaller marshallerMovimientos() {
        Jaxb2Marshaller marshallerMovimientos = new Jaxb2Marshaller();
        marshallerMovimientos.setContextPath("uy.com.md.global.soap.client.aamdsoapobtenermismovimientos");
        return marshallerMovimientos;
    }


    @Bean
    public GlobalClient globalMovimientosClient(Jaxb2Marshaller marshallerMovimientos) {
        GlobalClient client = new GlobalClient();
        client.setDefaultUri(globalMovimientosSoapUri);
        client.setMarshaller(marshallerMovimientos);
        client.setUnmarshaller(marshallerMovimientos);
        return client;
    }
}
