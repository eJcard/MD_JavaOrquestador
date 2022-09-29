package uy.com.md.common.util;

import static uy.com.md.common.util.Constantes.*;
import static uy.com.md.common.util.Constantes.SISTARBANC_NAME;

public class ProcesadorUtil {

    public static String obtenerProcesador(String value) {
        String procesador;
        switch (value.toUpperCase()) {
            case Constantes.JCARD_NAME:
                procesador = Constantes.JCARD_NAME;
                break;
            case Constantes.GLOBAL_NAME:
                procesador = Constantes.GLOBAL_NAME;
                break;
            case Constantes.SISTARBANC_NAME:
                procesador = Constantes.SISTARBANC_NAME;
                break;
            default:
                throw new UnsupportedOperationException(String.format("No existe valor para el procesador: %s.", value));
        }
        return procesador;
    }



    public static String obtenerProcesadorSub(String senderRef) {
        String resultado;
        senderRef = senderRef.substring(0, 3);
        switch (senderRef) {
            case JCARD_SENDERREF:
                resultado = JCARD_NAME;
                break;
            case SISTARBANC_SENDERREF:
                resultado = SISTARBANC_NAME;
                break;
            default:
                resultado = GLOBAL_NAME;
        }
        return resultado;
    }
}
