package uy.com.md.common.util;

public class StringPaddingUtil {

    public static String completarConCerosALaIzquierdaHastaDiez(String cadena) {
        return String.format("%10s", cadena).replace(' ', '0');
    }
}
