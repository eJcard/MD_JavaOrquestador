package uy.com.md.sistar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    UsernameInterceptor usernameInterceptor() {
        return new UsernameInterceptor();
    }

    @Bean
    AuthorizationInterceptor authTokenInterceptor () {
        return new AuthorizationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(usernameInterceptor());
        registry.addInterceptor(authTokenInterceptor());
    }

}