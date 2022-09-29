package uy.com.md.sistar.service;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uy.com.md.common.MessageDetailExtended;
import uy.com.md.mensajes.dto.InternalResponseDto;
import uy.com.md.mensajes.dto.MensajeDetail;
import uy.com.md.mensajes.dto.MensajeOrigen;
import uy.com.md.mensajes.service.MensajeService;
import uy.com.md.mensajes.util.Origenes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ServiceUtils {

  public static final String ERROR_UNEXPECTED = "error.unexpected";
  private final MensajeService messageService;

  @Autowired
  private SistarServiceProperties config;

  public ServiceUtils(MensajeService messageService){
    this.messageService = messageService;
  }

  protected MensajeDetail getMensajeDetail(long errorCode, String errorDescription) {
    return getMensajeDetail(String.valueOf(errorCode), errorDescription);
  }

  public MensajeDetail getMensajeDetail(String errorCode, String errorDescription) {
    return messageService.obtenerMensajeDetail(new MensajeOrigen(Origenes.SISTAR, errorCode, errorDescription));
  }

  public MessageDetailExtended getMensajeDetail(String errorCode, String errorDescription, String trace) {
    MensajeDetail detail = messageService.obtenerMensajeDetail(new MensajeOrigen(Origenes.SISTAR, errorCode, errorDescription));
    return new MessageDetailExtended(detail.getOrigen(), detail.getCodigo(), detail.getMensaje(), detail.getTipo(), trace);
  }

  protected MensajeDetail exceptionMessage(Exception ex) {
    return getMensajeDetail("", ex.getMessage());
  }

  protected <T, U> InternalResponseDto<T> circuitOpenErrorResponse(U requestDto, CallNotPermittedException ex) {
    InternalResponseDto<T> errResponse = new InternalResponseDto<>(null);
    errResponse.setResultado(false);
    errResponse.addMensaje(messageService.obtenerMensajeDetail(new MensajeOrigen(Origenes.SISTAR, "circuit.open", "call not permitted")));
    return errResponse;
  }

  protected <T, U> InternalResponseDto<T> unknownErrorResponse(U requestDto, Exception ex) {
    InternalResponseDto<T> errResponse = new InternalResponseDto<>(null);
    errResponse.setResultado(false);
    Throwable rootCause = ExceptionUtils.getRootCause(ex);
    rootCause = rootCause == null ? ex: rootCause;
    String origDesc = ex.getMessage();
    String code = config.getErr_code_by_root_exception().getOrDefault(rootCause.getClass().getTypeName(), ERROR_UNEXPECTED);
    String desc = rootCause.getMessage();
    errResponse.addMensaje(messageService.obtenerMensajeDetail(new MensajeOrigen(Origenes.SISTAR, code, desc)));
    return errResponse;
  }

  protected <T, U> InternalResponseDto<T> unknownErrorResponse(U requestDto, SistarbancOperationException ex) {
    InternalResponseDto<T> errResponse = new InternalResponseDto<>(null);
    errResponse.setResultado(false);
    errResponse.addMensaje(messageService.obtenerMensajeDetail(new MensajeOrigen(Origenes.SISTAR, ex.getCode(), ex.getMessage())));
    return errResponse;
  }

  protected <T, U> InternalResponseDto<T> crackResponse(U requestDto, Exception ex) {
    Optional<Method> method = Arrays.stream(this.getClass().getDeclaredMethods())
        .filter(m -> m.getName().endsWith("ErrorResponse"))
        .filter(m -> m.getParameterTypes().length > 1 && m.getParameterTypes()[1].getName().equals(ex.getClass().getName()))
        .findFirst();
    if(method.isPresent()){
      try {
        Class<?> clazz = Class.forName(method.get().getParameterTypes()[1].getName());
        return (InternalResponseDto<T>) method.get().invoke(this, requestDto, clazz.cast(ex));
      } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }

    return  this.unknownErrorResponse(requestDto, ex);
  }

  /**
   * Extrae una porción de descripción si esta marchea con alguna de las expresiones regulares configuradas (property <b>sistar.result_message_filter</b>).
   * Se espera que la expresion contenga un grupo con nombre <i>message</i>.
   * @param obj Instancia que provee la descripción. Para obtener el string, se invoca el método toString del objeto.
   * @return Una subcadena de la cadena que representa el parámetro obj, o la cadena completa en caso que esta no matchee con ninguna de las expresiones regulares configuradas.
   * Ejemplo de expresión regular admitida: <i>(.*: )?(?<message>.*?)\\. ---></>
   */
  public String getShortDescription(Object obj) {
    if(obj == null){
      return null;
    }
    String msg = obj.toString();
    Optional<String> matched = config.getResult_message_patterns()
        .stream()
        .map(Pattern::compile)
        .map(p -> p.matcher(msg))
        .filter(Matcher::find)
        .map(m -> m.group("message"))
        .findFirst();

    return matched.orElse(msg);
  }
}
