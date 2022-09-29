package uy.com.md.common.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import uy.com.md.common.enumerated.ErrorType;
import uy.com.md.common.enumerated.MDErrorMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

///**
//* @deprecated en su lugar debe empesar a usarse uy.com.md.common.enumerated.MensajeType.
//*/
@Deprecated
public class ErrorDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonProperty("mensaje")
    private String mensaje;

    @JsonProperty("codigo")
    private Integer codigo;

    @JsonProperty("tipo")
    private ErrorType tipo;

    @JsonProperty("mensajes")
    private List<String> mensajes = new ArrayList<>();

    protected ErrorDetail(String stackTrace, MDErrorMessage MDErrorMessage, ErrorType tipo) {
        super();
        if (MDErrorMessage != null) {
            this.mensaje = MDErrorMessage.getMessage();
            this.codigo = MDErrorMessage.getCode();
        }
        this.tipo = tipo;

    }

    public ErrorDetail(MDErrorMessage MDErrorMessage, String customMessage, ErrorType tipo) {
        super();
        if (MDErrorMessage != null) {
            this.mensaje = MDErrorMessage.getMessage();
            this.codigo = MDErrorMessage.getCode();
            if (!customMessage.isEmpty())
                this.mensaje = this.mensaje + " - " + customMessage;
        }
        this.tipo = tipo;
    }

    public ErrorDetail(MDErrorMessage MDErrorMessage, List<String> customMessages,
                       ErrorType tipo) {
        super();
        if (MDErrorMessage != null) {
            this.mensaje = MDErrorMessage.getMessage();
            this.codigo = MDErrorMessage.getCode();
            this.mensajes = customMessages;
        }
        this.tipo = tipo;
    }

    public ErrorDetail(MDErrorMessage MDErrorMessage, ErrorType tipo, String... arg1) {
        super();
        if (MDErrorMessage != null) {
            this.mensaje = String.format(MDErrorMessage.getMessage(), arg1);
            this.codigo = MDErrorMessage.getCode();
        }
        this.tipo = tipo;
    }

    public ErrorDetail(MDErrorMessage MDErrorMessage, List<String> customMessages, ErrorType tipo,
                       String... arg1) {
        super();
        if (MDErrorMessage != null) {
            this.mensaje = String.format(MDErrorMessage.getMessage(), arg1);
            this.codigo = MDErrorMessage.getCode();
            this.mensajes = customMessages;
        }
        this.tipo = tipo;
    }

    public ErrorDetail(MDErrorMessage MDErrorMessage, ErrorType tipo) {
        super();
        if (MDErrorMessage != null) {
            this.mensaje = MDErrorMessage.getMessage();
            this.codigo = MDErrorMessage.getCode();
        }
        this.tipo = tipo;
    }

    public ErrorDetail(String errorMessage, Integer codigo, String stackTrace, ErrorType tipo) {
        super();
        this.mensaje = errorMessage;
        this.codigo = codigo;
        this.tipo = tipo;
    }
    
    public ErrorDetail() {
    }
    

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public ErrorType getTipo() {
        return tipo;
    }

    public void setTipo(ErrorType tipo) {
        this.tipo = tipo;
    }

    public List<String> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<String> mensajes) {
        this.mensajes = mensajes;
    }
}
