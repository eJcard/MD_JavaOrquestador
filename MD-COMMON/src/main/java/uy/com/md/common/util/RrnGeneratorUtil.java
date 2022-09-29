package uy.com.md.common.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static uy.com.md.common.util.Constantes.*;

public class RrnGeneratorUtil {

    private static final String PATRON = "%1$s%2$s";

    private RrnGeneratorUtil() {
        throw new IllegalStateException("RrnGeneratorUtil utility class");
    }

    public static String generate(String pdpUUID) {
        Long transactionId = generateUniqueId(pdpUUID);
        return TxnId.create(LocalDateTime.now(), 0, transactionId).toRrn();
    }

    private static Long generateUniqueId(String pdpUUID) {
        final UUID uid = UUID.nameUUIDFromBytes(pdpUUID.getBytes());
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uid.getLeastSignificantBits());
        buffer.putLong(uid.getMostSignificantBits());
        final BigInteger bi = new BigInteger(buffer.array());
        return bi.longValue();
    }

    public static String createCompositeRrn(String procesador, String rrn) {
        String respuesta;
        switch (ProcesadorUtil.obtenerProcesador(procesador)) {
            case "JCARD":
                respuesta = String.format(PATRON, JCARD_SENDERREF, rrn);
                break;
            case "GLOBAL":
                respuesta = String.format(PATRON, GLOBAL_SENDERREF, rrn);
                break;
            case "SISTARBANC":
                respuesta = String.format(PATRON, SISTARBANC_NAME, rrn);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return respuesta;
    }

    public static Map<String, String> decodeCompositeRrn(String identifcador) {
        Map<String, String> resultado = new HashMap<>();
        resultado.put("procesador", ProcesadorUtil.obtenerProcesadorSub(identifcador));
        resultado.put("codigo_autorizacion", identifcador);
        resultado.put("procesador_prefijo", identifcador.substring(0, 3));
        return resultado;
    }
    
    public static Long getTransactionId(String rrn) {
    	Long rrnTransactionId = null;
    	if(Objects.nonNull(rrn)) {    		
    		try {
        		rrnTransactionId = Long.parseLong(rrn);					        	
            } catch (NumberFormatException exception) {
            	rrnTransactionId = Long.parseLong(rrn, 36);        	
            }     		
    	}
		return rrnTransactionId; 	
    }
    
    

}
