package uy.com.md.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import uy.com.md.global.soap.GlobalClient;

@Configuration
public class GlobalEntregaClientConfig {
	
	@Value("${global.entrega.soap.uri}")
    public String entregaGlobalSoapUri;

    @Bean
    public Jaxb2Marshaller marshallerEntrega() {
        Jaxb2Marshaller marshallerEntrega = new Jaxb2Marshaller();
        marshallerEntrega.setContextPath("uy.com.md.global.soap.client.aamdsoapentreganominada");
        return marshallerEntrega;
    }
    
    @Bean
    public GlobalClient globalEntregaClient(Jaxb2Marshaller marshallerEntrega) {
        GlobalClient client = new GlobalClient();
        client.setDefaultUri(entregaGlobalSoapUri);
        client.setMarshaller(marshallerEntrega);
        client.setUnmarshaller(marshallerEntrega);
        return client;
    }

}
