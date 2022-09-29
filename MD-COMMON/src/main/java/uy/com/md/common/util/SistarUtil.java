package uy.com.md.common.util;

public class SistarUtil {

    public static String obtenerEmisor(Long marca) {
        return marca == 1 ? String.valueOf(36) : String.valueOf(37);
    }

    public static Long obtenerMarca(Long sello) {
        return sello == 1L ? 3L : 1L;
    }
}
