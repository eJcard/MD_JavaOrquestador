package uy.com.md.mensajes.enumerated;

import uy.com.md.mensajes.interfaces.MensajeNegocioInterface;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Mensajes implements MensajeNegocioInterface {
    /**
     * GENERAL_ERROR 0 a 999
     */
    DEFAULT_MESSAGE("1"), // Ha ocurrido un error inesperado

    /**
     * ERROR_GENERICO_* de 1000 a 1999
     */
    ERROR_GENERICO("1000"), // Erro generico

    /**
     * MD_AUTORIZACION_* de 2000 a 2999
     */
    MD_AUTORIZACION_ERROR("2000"), // Error en MD Autorizacion

    /**
     * MD_CUMPLIMIENTO_* de 3000 a 3999
     */
    MD_CUMPLIMIENTO_ERROR("3000"), // Error en MD Cumplimiento

    /**
     * MD_JCARD_* de 4000 a 4999
     */
    MD_JCARD_ERROR("4000"), // Error en MD JCARD
    MD_JCARD_ERROR_CONSUMIENDO("4001"), // Error en MD JCARD arrojado por el WS jCard
    MD_JCARD_ERROR_RED("4003"),
    /**
     * MD_GLOBAL_* de 5000 a 5999
     */
    MD_GLOBAL_ERROR("5000"), // Error en MD GLOBAL

    /**
     * MD_CONTRATO_* de 6000 a 6999
     */
    MD_CONTRATO_ERROR("6000"), // Error en MD CONTRATO

    /**
     * MD_PERSONA_* de 7000 a 7999
     */
    MD_PERSONA_ERROR("7000"), // Error en MD PERSONA

    /**
     * MD_SEGURIDAD_* de 8000 a 8999
     */
    MD_SEGURIDAD_ERROR("8000"), // Error en MD SEGURIDAD
    MD_SEGURIDAD_ERROR_AUTORIZADO("8001"),
    MD_SEGURIDAD_ERROR_TOKEN("8002"),
    MD_SEGURIDAD_ERROR_IDEMPOTENCIA("8003"),
    MD_SEGURIDAD_ERROR_REINTENTO("8004"),

    /**
     * MD_PRODUCTO_* de 9000 a 9999
     */
    MD_PRODUCTO_ERROR("9000"), // Error en MD PRODUCTO
    MD_PRODUCTO_ERROR_TOKEN("9001"), // El token para el usuario es incorrecto
    MD_PRODUCTO_ERROR_INVALID_PARAMETER("9002"), // El token para el usuario es incorrecto


    /**
     * MD_SISTARBANC_* de 10000 a 10999
     */
    MD_SISTARBANC_ERROR("10000"),  // Error en MD SISTARBANC

    /**
     * MD_BASICOPRESENCIAL_* de 11000 a 11999
     */
    MD_BASICOPRESENCIAL_ERROR("11000"),  // Error en MD BASICOPRESENCIAl

    /**
     * MD_BASICODIGITAL_* de 12000 a 12999
     */
    MD_BASICODIGITAL_ERROR("12000"),  // Error en MD BASICODIGITAL
    MD_BASICODIGITAL_SIN_PRODUCTO_ERROR("12001"),
    MD_BASICODIGITAL_TOKEN_INVALIDO_ERROR("12002"),
    MD_BASICODIGITAL_CONTROL_CUENTA_ERROR("12003"),
    MD_BASICODIGITAL_MTB_ERROR("12004"),


    /**
     * MD_CATALOGO_* de 13000 a 13999
     */
    MD_CATALOGO_ERROR("13000"),  // Error en MD CATALOGO

    /**
     * MD_MOVIMIENTOS_* de 14000 a 14999
     */
    MD_MOVIMIENTOS_ERROR("14000"), // Error en MD MOVIMIENTOS
    MD_MOVIMIENTOS_CREAR_ERROR("14001");

    private final String codigoNegocio;

    private Mensajes(String codigoNegocio) {
        this.codigoNegocio = codigoNegocio;
    }

    private static final Map<String, Mensajes> CODIGOS = new HashMap<>();

    static {
        Arrays.asList(values()).forEach(e -> CODIGOS.put(e.codigoNegocio, e));
    }

    public static Mensajes valueOfCodigoNegocio(String codigo) {
        return CODIGOS.get(codigo);
    }

    @Override
    public String getCodigoNegocio() {
        return this.codigoNegocio;
    }

}
