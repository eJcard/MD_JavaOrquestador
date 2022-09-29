package uy.com.md.global;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"uy.com.md", "net.atos.external.config"})
//@EnableDiscoveryClient
public class MdGlobalApplication {

	public static void main(String[] args) {
		SpringApplication.run(MdGlobalApplication.class, args);
	}

}
