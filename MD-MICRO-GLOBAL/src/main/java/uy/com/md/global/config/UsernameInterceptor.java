package uy.com.md.global.config;

import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static uy.com.md.common.util.Constantes.REQUEST_UACT;


public class UsernameInterceptor extends HandlerInterceptorAdapter implements ClientHttpRequestInterceptor {

    private static final String USERNAME = "ANNONYMOUS";
    private static final String UACT = "uact";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uact = request.getHeader(REQUEST_UACT) == null ? USERNAME : request.getHeader(REQUEST_UACT);
        MDC.put(UACT, uact);
        return true;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        return null;
    }
}
