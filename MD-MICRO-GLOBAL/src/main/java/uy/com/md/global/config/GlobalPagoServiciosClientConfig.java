package uy.com.md.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import uy.com.md.global.soap.GlobalClient;

@Configuration
public class GlobalPagoServiciosClientConfig {

    @Value("${global.pago.servicios.soap.uri}")
    public String globalPagoServiciosSoapUri;

    @Bean
    public Jaxb2Marshaller marshallerPagoServicios() {
        Jaxb2Marshaller marshallerPagoServicios = new Jaxb2Marshaller();
        marshallerPagoServicios.setContextPath("uy.com.md.global.soap.client.aamdsoapautorizarpago");
        return marshallerPagoServicios;
    }


    @Bean
    public GlobalClient globalPagoServiciosClient(Jaxb2Marshaller marshallerPagoServicios) {
        GlobalClient client = new GlobalClient();
        client.setDefaultUri(globalPagoServiciosSoapUri);
        client.setMarshaller(marshallerPagoServicios);
        client.setUnmarshaller(marshallerPagoServicios);
        return client;
    }
}
