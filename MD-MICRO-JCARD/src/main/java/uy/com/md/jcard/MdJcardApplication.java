package uy.com.md.jcard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"uy.com.md", "net.atos.external.config"})
//@EnableDiscoveryClient
@EnableFeignClients
public class MdJcardApplication {

	public static void main(String[] args) {
		SpringApplication.run(MdJcardApplication.class, args);
	}

}
