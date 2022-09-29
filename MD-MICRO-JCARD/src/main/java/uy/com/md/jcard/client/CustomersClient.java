package uy.com.md.jcard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import uy.com.md.jcard.config.JcardClientConfig;
import uy.com.md.jcard.config.JcardRequestException;
import uy.com.md.jcard.dto.info.ClienteResponseDto;
import uy.com.md.jcard.util.Constantes;

@FeignClient(value = "customers", url = "${jcard.customers.endpoint}", configuration = JcardClientConfig.class)
public interface CustomersClient {

    @GetMapping("/customers/{realId}")
    ClienteResponseDto customerInfo(@RequestHeader(Constantes.AUTH_TOKEN) String bearerToken, @RequestHeader(Constantes.JCARD_HEADERS_VERSION) String version, @RequestHeader(Constantes.JCARD_HEADERS_CONSUMER_ID) String consumerId, @PathVariable(name = "realId") String realId) throws JcardRequestException;
}
