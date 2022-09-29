package uy.com.md.sistar.config;

import net.atos.aspect.AtosTraceAspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Component
@Aspect
public class ApiTrace extends AtosTraceAspect {

    // TODO: Configurar de acuerdo al namespace global del proyecto
    public static final String TRACE_PACKAGE = "execution(* uy.com.md..*.* (..))";


    @Pointcut(TRACE_PACKAGE)
    public void resources() {
    }

    @Before("resources()")
    public void apiRequestLog(JoinPoint jp) {
        super.apiRequestLog(jp);
    }

    @AfterReturning(pointcut = "resources()", returning = "returnValue")
    public void apiResponseLog(JoinPoint jp, Object returnValue) {
        super.apiResponseLog(jp, returnValue);
    }

    @AfterThrowing(pointcut = "resources()", throwing = "exception")
    public void apiResponseExceptionLog(JoinPoint jp, Exception exception) {
        super.apiResponseExceptionLog(jp, exception);
    }
}
