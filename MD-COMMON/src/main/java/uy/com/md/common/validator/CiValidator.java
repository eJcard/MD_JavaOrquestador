package uy.com.md.common.validator;

public class CiValidator {

    private CiValidator() {
        throw new IllegalStateException("CiValidator utility class");
    }

    public static boolean validarCi(String ciCompleta) {
        boolean isValid = false;
        String completa = ciCompleta.trim();
        if (completa.length() > 1) {
            isValid = ciValida(completa.substring(0, completa.length() - 1),
                    completa.substring(completa.length() - 1));
        } else {
            isValid = false;
        }
        return isValid;
    }


    public static boolean ciValida(String numero, String pintDigito) {
        boolean isValid = false;

        int pintNumeroResultante = 0;
        Integer pintDigitoResultante = 0;
        Integer tope;
        Integer[] modulo = new Integer[7];
        Integer[] vecDocumento = new Integer[7];
        Integer[] vecResultante = new Integer[7];
        Integer j = 0;


        if (numero.length() < 2 || numero.length() > 7) {
            return isValid;
        }

        tope = numero.length() - 1;
        modulo[0] = 2;
        modulo[1] = 9;
        modulo[2] = 8;
        modulo[3] = 7;
        modulo[4] = 6;
        modulo[5] = 3;
        modulo[6] = 4;

        numero = numero.trim();


        for (int i = 0; i <= tope; i++) {
            vecDocumento[i] = Integer.parseInt(numero.substring((i), i + 1));
        }


        for (int i = 6; i >= 6 - tope; i--) {


            int indexVec = i - (6 - tope);
            vecResultante[j] = Integer.parseInt(Integer
                    .valueOf(modulo[i] * vecDocumento[indexVec])
                    .toString()
                    .substring(
                            Integer.valueOf(
                                    modulo[i] * vecDocumento[i - (6 - tope)])
                                    .toString().length() - 1));
            j++;
        }


        for (int i = 0; i <= tope; i++) {
            pintNumeroResultante = pintNumeroResultante + vecResultante[i];
        }


        pintDigitoResultante = proxDecena(pintNumeroResultante)
                - pintNumeroResultante;


        isValid = Integer.parseInt(pintDigito) == Integer
                .parseInt(pintDigitoResultante.toString().substring(
                        pintDigitoResultante.toString().length() - 1,
                        pintDigitoResultante.toString().length()));
        return isValid;
    }

    protected static int proxDecena(Integer numero) {
        Integer i;
        int resultado = 0;
        if (numero % 10 == 0) {
            resultado = numero + 10;
        } else {
            i = numero;
            while (!(i % 10 == 0)) {
                i = i + 1;
            }
            resultado = i;
        }
        return resultado;
    }

}
