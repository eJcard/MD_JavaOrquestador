package uy.com.md.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import uy.com.md.global.soap.GlobalClient;

@Configuration
public class GlobalTransferenciaC2CClientConfig {

    @Value("${global.transferencia.c2c.soap.uri}")
    public String globalTransferenciaC2CSoapUri;

    @Bean
    public Jaxb2Marshaller marshallerTransferenciaC2C() {
        Jaxb2Marshaller marshallerTransferenciaC2C = new Jaxb2Marshaller();
        marshallerTransferenciaC2C.setContextPath("uy.com.md.global.soap.client.aamdsoaptransferirc2c");
        return marshallerTransferenciaC2C;
    }

    @Bean
    public GlobalClient globalTransferenciaC2CClient(Jaxb2Marshaller marshallerTransferenciaC2C) {
        GlobalClient client = new GlobalClient();
        client.setDefaultUri(globalTransferenciaC2CSoapUri);
        client.setMarshaller(marshallerTransferenciaC2C);
        client.setUnmarshaller(marshallerTransferenciaC2C);
        return client;
    }
}