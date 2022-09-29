package uy.com.md.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import uy.com.md.global.soap.GlobalClient;

@Configuration
public class GlobalTransferenciaC2BClientConfig {

    @Value("${global.transferencia.c2b.soap.uri}")
    public String globalTransferenciaC2BSoapUri;

    @Bean
    public Jaxb2Marshaller marshallerTransferenciaC2B() {
        Jaxb2Marshaller marshallerTransferenciaC2B = new Jaxb2Marshaller();
        marshallerTransferenciaC2B.setContextPath("uy.com.md.global.soap.client.aamdsoaptransferirc2banco");
        return marshallerTransferenciaC2B;
    }

    @Bean
    public GlobalClient globalTransferenciaC2BClient(Jaxb2Marshaller marshallerTransferenciaC2B) {
        GlobalClient client = new GlobalClient();
        client.setDefaultUri(globalTransferenciaC2BSoapUri);
        client.setMarshaller(marshallerTransferenciaC2B);
        client.setUnmarshaller(marshallerTransferenciaC2B);
        return client;
    }
}