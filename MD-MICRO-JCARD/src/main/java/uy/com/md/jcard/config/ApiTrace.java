package uy.com.md.jcard.config;


import net.atos.aspect.AtosTraceAspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
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
