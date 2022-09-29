package uy.com.md.sistar.annotation;

import java.lang.annotation.Documented;

import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.Parameter;

@Parameter(example = "370100000113")
@Size(min = 12, max = 19)
@Documented
public @interface AccountNumber {

}