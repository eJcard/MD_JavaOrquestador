package uy.com.md.common.util;

public class MotivoUtil {
	
	public static String parseMotivoToJCard(String motivo) {        
        switch (motivo) {
            case "OTR":
            	motivo = "otros";
                break;
            case "PAP":
            	motivo = "proveedores";
                break;
            case "PDA":
            	motivo = "alquileres";
                break;
            case "PHP":
            	motivo = "honorariosProf";
                break;
            case "PJP":
            	motivo = "JubilacionesYPensiones";
                break;
            case "PRE":
            	motivo = "PrestacionesSociales";
                break;
            case "PSP":
            	motivo = "serviciosPersonales";
                break;
            case "PSS":
            	motivo = "sueldos";
                break;
            case "TDD":
            	motivo = "traspasoDinero";
                break;
            case "TRN":
            	motivo = "transferencia";
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return motivo;
    }
    
}
