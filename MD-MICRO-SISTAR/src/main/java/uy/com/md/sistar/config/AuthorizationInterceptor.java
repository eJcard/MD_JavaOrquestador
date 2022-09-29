package uy.com.md.sistar.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import uy.com.md.sistar.controller.AuthorizationInfoBean;

public class AuthorizationInterceptor extends HandlerInterceptorAdapter{
  @Autowired
  private AuthorizationInfoBean authorizationInfo;

  @Override
  public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
    String token = request.getHeader("authorization");
    authorizationInfo.setToken(token);
    return true;
  }
}
