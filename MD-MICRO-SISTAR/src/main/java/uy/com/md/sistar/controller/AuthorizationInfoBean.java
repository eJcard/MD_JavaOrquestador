package uy.com.md.sistar.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.Data;

@Component
@RequestScope()
@Data
public class AuthorizationInfoBean {
  String token;
}
