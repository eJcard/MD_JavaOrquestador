package uy.com.md.common.util;

public class OperacionUtil {

    public static String obtenerOperacion(String value) {
        String operacion;
        switch (value.toUpperCase()) {
            case Constantes.SOLICITUD_NOMINADA:
                operacion = Constantes.SOLICITUD_NOMINADA;
                break;
            case Constantes.RECARGA:
                operacion = Constantes.RECARGA;
                break;
            case Constantes.ENTREGA_NOMINADA:
                operacion = Constantes.ENTREGA_NOMINADA;
                break;
            case Constantes.MOVIMIENTOS:
                operacion = Constantes.MOVIMIENTOS;
                break;
            case Constantes.TRANSFERENCIA_C2C:
                operacion = Constantes.TRANSFERENCIA_C2C;
                break;
            case Constantes.TRANSFERENCIA_C2B:
                operacion = Constantes.TRANSFERENCIA_C2B;
                break;
            case Constantes.PAGO_SERVICIOS:
                operacion = Constantes.PAGO_SERVICIOS;
                break;
            case Constantes.ALTA_TRANSFERENCIA:
                operacion = Constantes.ALTA_TRANSFERENCIA;
                break;
            case Constantes.RESULTADO_TRANSFERENCIA:
                operacion = Constantes.RESULTADO_TRANSFERENCIA;
                break;
            case Constantes.SOLICITUD_INNOMINADA:
                operacion = Constantes.SOLICITUD_INNOMINADA;
                break;
            case Constantes.REPOSICION_INNOMINADA:
                operacion = Constantes.REPOSICION_INNOMINADA;
                break;
            default:
                throw new UnsupportedOperationException(String.format("La operación %s no esta configurada.", value));
        }

        return operacion;
    }
}
