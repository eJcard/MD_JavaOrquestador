package uy.com.md.common.util;

public class CommonUtil {
	
	//TODO: Validar esto, puede ser -15 porque son las últimas 16 posiciones
    public static String obtenerNumeroSobre(String track) {
        return track.substring(track.length() - 16);
    }
    
    public static String obtenerProducto(String track) {
        return track.substring(0,3);
    }
    
    public static String formatDocumento(String paisDoc, String tipoDoc, String numDoc ) {
        return String.format("%s-%s-%s", paisDoc, tipoDoc, numDoc);
    }
    
}
