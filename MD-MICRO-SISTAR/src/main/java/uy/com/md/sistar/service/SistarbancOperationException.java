package uy.com.md.sistar.service;

import uy.com.md.mensajes.enumerated.Mensajes;

public class SistarbancOperationException extends RuntimeException {

  private final String code;

  public String getCode(){
    return code;
  }

  public SistarbancOperationException(String code, String message){
    super(message);
    this.code = code;
  }

  public SistarbancOperationException(int code, String message){
    super(message);
    this.code = String.valueOf(code);
  }

  public SistarbancOperationException(Mensajes code, String message){
    super(message);
    this.code = code.getCodigoNegocio();
  }

  public SistarbancOperationException(long code, String message){
    super(message);
    this.code = String.valueOf(code);
  }

  public SistarbancOperationException(Long code, String message){
    super(message);
    this.code = (code == null) ? null : code.toString();
  }

  public SistarbancOperationException(String code, String message, Throwable cause){
    super(message, cause);
    this.code = code;
  }

  public SistarbancOperationException(String message){
    this((String)null, message);
  }
}
