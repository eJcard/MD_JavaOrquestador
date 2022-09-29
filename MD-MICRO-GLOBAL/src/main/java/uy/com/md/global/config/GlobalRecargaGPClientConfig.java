package uy.com.md.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import uy.com.md.global.soap.GlobalClient;

@Configuration
public class GlobalRecargaGPClientConfig {

    @Value("${global.recargagp.soap.uri}")
    public String globalRecargaGPSoapUri;

    @Bean
    public Jaxb2Marshaller marshallerRecargaGP() {
        Jaxb2Marshaller marshallerRecargaGP = new Jaxb2Marshaller();
        marshallerRecargaGP.setContextPath("uy.com.md.global.soap.client.aamdsoaprecargargp");
        return marshallerRecargaGP;
    }


    @Bean
    public GlobalClient globalRecargaGPClient(Jaxb2Marshaller marshallerRecargaGP) {
        GlobalClient client = new GlobalClient();
        client.setDefaultUri(globalRecargaGPSoapUri);
        client.setMarshaller(marshallerRecargaGP);
        client.setUnmarshaller(marshallerRecargaGP);
        return client;
    }
}
