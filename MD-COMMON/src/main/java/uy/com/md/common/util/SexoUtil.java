package uy.com.md.common.util;

public class SexoUtil {

    public static String mapToSexo(String value) {
        String sexo = "";
        switch (value) {
            case "m":
            case "MAS":
                sexo = "MAS";
                break;
            case "f":
            case "FEM":
                sexo = "FEM";
                break;
            case "otro":
            case "OTRO":
            case "OTR":
            case "otr":
                sexo = "OTR";
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return sexo;
    }

    public static String mapToSexoJcard(String value) {
        String sexo = "";
        switch (value) {
            case "m":
            case "MAS":
                sexo = "m";
                break;
            case "f":
            case "FEM":
                sexo = "f";
                break;
            case "otro":
            case "OTRO":
                sexo = "otro";
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return sexo;
    }
}
