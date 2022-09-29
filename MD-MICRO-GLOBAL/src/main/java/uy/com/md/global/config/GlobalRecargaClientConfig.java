package uy.com.md.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import uy.com.md.global.soap.GlobalClient;

@Configuration
public class GlobalRecargaClientConfig {

    @Value("${global.recarga.soap.uri}")
    public String globalRecargaSoapUri;

    @Bean
    public Jaxb2Marshaller marshallerRecarga() {
        Jaxb2Marshaller marshallerRecarga = new Jaxb2Marshaller();
        marshallerRecarga.setContextPath("uy.com.md.global.soap.client.aamdsoaprecargartarjeta");
        return marshallerRecarga;
    }


    @Bean
    public GlobalClient globalRecargaClient(Jaxb2Marshaller marshallerRecarga) {
        GlobalClient client = new GlobalClient();
        client.setDefaultUri(globalRecargaSoapUri);
        client.setMarshaller(marshallerRecarga);
        client.setUnmarshaller(marshallerRecarga);
        return client;
    }
}
