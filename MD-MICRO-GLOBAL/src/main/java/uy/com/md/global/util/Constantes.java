package uy.com.md.global.util;

public final class Constantes {

    private Constantes() {
    }

    public static final String AUTH_TOKEN = "Authorization";
    public static final String GLOBAL_HEADERS_SUBAGENCIA = "pdp-subagencia";
    public static final String GLOBAL_HEADERS_CAJA = "pdp-caja";
    public static final String GLOBAL_HEADERS_UUID = "pdp-uuid";
    public static final String AMD_TOKEN = "amd-token";
    public static final String GLOBAL_HEADERS_AMD_CLIENTE = "amd-cliente";
    public static final String GLOBAL_HEADERS_AMD_REQUEST_ID= "amd-requestId";
    public static final String GLOBAL_HEADERS_AMD_SISTEMA= "amd-sistema";
    public static final String GLOBAL_HEADERS_USUARIO = "pdp-usuario";
    public static final String GLOBAL_SOAP_ACTION_SOLICITUD = "ApiMidineroaction/AAMDSOAPSOLICITUDPRODUCTONOMINADOV2.Execute";    
    public static final String GLOBAL_SOAP_ACTION_ENTREGA = "ApiMidineroaction/AAMDSOAPENTREGANOMINADA.Execute";
    public static final String GLOBAL_SOAP_ACTION_RECARGA = "ApiMidineroaction/AAMDSOAPRECARGARTARJETA.Execute";
    public static final String GLOBAL_SOAP_ACTION_MOVIMIENTOS = "ApiMidineroaction/AAMDSOAPOBTENERMISMOVIMIENTOSV2.Execute";
    public static final String GLOBAL_SOAP_ACTION_TRANSFERENCIA_C2C = "ApiMidineroaction/AAMDSOAPTRANSFERIRC2C.Execute";
    public static final String GLOBAL_SOAP_ACTION_PAGO_SERVICIOS = "ApiMidineroaction/AAMDSOAPAUTORIZARPAGO.Execute";
    public static final String GLOBAL_SOAP_ACTION_TRANSFERENCIA_C2B = "ApiMidineroaction/AAMDSOAPTRANSFERIRC2BANCO.Execute";
    public static final String GLOBAL_SOAP_ACTION_RECARGA_GP = "ApiMidineroaction/AAMDSOAPRECARGARGP.Execute";
}