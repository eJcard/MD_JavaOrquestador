package uy.com.md.sistar.config;

import static uy.com.md.common.util.Constantes.REQUEST_UACT;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class UsernameInterceptor extends HandlerInterceptorAdapter implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(UsernameInterceptor.class);
    private static final String USERNAME = "ANNONYMOUS";
    private static final String UACT = "uact";

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String uact = request.getHeader(REQUEST_UACT) == null ? USERNAME : request.getHeader(REQUEST_UACT);
        MDC.put(UACT, uact);
        return true;
    }

    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest httpRequest, @NonNull byte[] bytes, @NonNull ClientHttpRequestExecution clientHttpRequestExecution) {
        return null;
    }
}