package uy.com.md.common.util;

public final class Constantes {

    private Constantes() {
        super();
    }

	public static final String FORMATO_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS X zzz";
    public static final String AUTH_TOKEN = "Authorization";
    public static final String AUTH_TOKEN_TYPE_BEARER = "Bearer ";
    public static final String AUTH_TOKEN_TYPE_BASIC = "Basic ";

    public static final String JCARD_NAME = "JCARD";
    public static final String GLOBAL_NAME = "GLOBAL";
    public static final String SISTARBANC_NAME = "SISTARBANC";


    public static final String SOLICITUD_NOMINADA = "SOLICITUD_NOMINADA";

    public static final String RECARGA = "RECARGA";

    public static final String ENTREGA_NOMINADA = "ENTREGA_NOMINADA";

    public static final String MOVIMIENTOS = "MOVIMIENTOS";

    public static final String TRANSFERENCIA_C2C = "TRANSFERENCIA_C2C";

    public static final String PAGO_SERVICIOS = "PAGO_SERVICIOS";

    public static final String TRANSFERENCIA_C2B = "TRANSFERENCIA_C2B";

    public static final String ALTA_TRANSFERENCIA = "ALTA_TRANSFERENCIA";

    public static final String RESULTADO_TRANSFERENCIA = "RESULTADO_TRANSFERENCIA";

    public static final String RECARGANTES = "RECARGANTES";

    public static final String REQUEST_ID = "pdp-uuid";
    public static final String REQUEST_UACT = "md-uact";
    public static final String REQUEST_CONSUMER_APP = "md-consumer-app";
    
    public static final String AMD_TOKEN = "amd-token";
    public static final String AMD_REQUESTID = "amd-requestid";

    public static final String MD_ORIGEN = "md-origen";
    public static final String MD_CORRELATION_ID = "activityid";

    public static final int MD_AUTORIZACION_RED = 1;

    public static final String IDEMPOTENCIA_ESTADO_PROCESSING = "PROCESSING";

    public static final String GLOBAL_SENDERREF = "001";
    public static final String JCARD_SENDERREF = "002";
    public static final String SISTARBANC_SENDERREF = "003";

    public static final String PROCESADOR = "procesador";
    public static final String SENDERREF = "SenderRef";
    public static final String AMD_PAIS_DOCUMENTO = "amd-paisDocumento";
    public static final String AMD_USUARIO_EMAIL = "amd-usuarioEmail";

    public static final String AMD_CLIENT_APPLICATION = "amd-clientapplication";
    public static final String AMD_CLIENT_PLATFORM = "amd-clientplatform";
    public static final String AMD_CLIENT_DEVICE = "amd-clientdevice";
    public static final String AMD_CLIENT_IP = "amd-clientip";

    public static final String SOLICITUD_INNOMINADA = "SOLICITUD_INNOMINADA";
    public static final String REPOSICION_INNOMINADA = "REPOSICION_INNOMINADA";
}
