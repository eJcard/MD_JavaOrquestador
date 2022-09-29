package uy.com.md.persona;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
@ComponentScan({"uy.com.md", "net.atos.external.config"})
//@EnableDiscoveryClient
@EnableFeignClients
public class MdPersonaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MdPersonaApplication.class, args);
	}

	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
	    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
	    loggingFilter.setIncludeClientInfo(true);
	    loggingFilter.setIncludeQueryString(true);
	    loggingFilter.setIncludePayload(true);
	    loggingFilter.setMaxPayloadLength(64000);
	    return loggingFilter;
	}
}
