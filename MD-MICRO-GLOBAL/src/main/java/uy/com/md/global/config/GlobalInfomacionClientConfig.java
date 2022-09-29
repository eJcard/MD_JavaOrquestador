package uy.com.md.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import uy.com.md.global.soap.GlobalClient;

@Configuration
public class GlobalInfomacionClientConfig {

    @Value("${global.customers.soap.uri}")
    public String globalInformacionSoapUri;

    @Bean
    public Jaxb2Marshaller marshallerInformacion() {
        Jaxb2Marshaller marshallerInformacion = new Jaxb2Marshaller();
        marshallerInformacion.setContextPath("uy.com.md.global.soap.client.aamdsoapobtenerdatosxcuenta");
        return marshallerInformacion;
    }


    @Bean
    public GlobalClient globalInformacionClient(Jaxb2Marshaller marshallerInformacion) {
        GlobalClient client = new GlobalClient();
        client.setDefaultUri(globalInformacionSoapUri);
        client.setMarshaller(marshallerInformacion);
        client.setUnmarshaller(marshallerInformacion);
        return client;
    }
}
