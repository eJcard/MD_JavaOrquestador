package uy.com.md.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MonedaUtil {

    public static short converterMoneda(String value) {
        short moneda;
        switch (value) {
            case "1":
            case "858":
            case "UYU":
                moneda = 858;
                break;
            case "2":
            case "840":
            case "USU":
            case "USD":
                moneda = 840;
                break;
            default:
                moneda = Short.parseShort(value);
        }
        return moneda;
    }

    public static short converterMonedaParaProcesador(String procesador, String value) {
        short moneda;
        switch (procesador) {
            case Constantes.JCARD_NAME:
                moneda = converterMoneda(value);
                break;
            case Constantes.GLOBAL_NAME:
                moneda = converterMonedaParaProcesadorGlobal(value);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return moneda;
    }


    public static short converterMonedaParaProcesadorGlobal(String value) {
        short moneda;
        switch (value) {
            case "1":
            case "858":
            case "UYU":
                moneda = 1;
                break;
            case "2":
            case "840":
            case "USU":
            case "USD":
                moneda = 2;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return moneda;
    }

    public static String monedaFormater(BigDecimal value) {
        value = value.setScale(2, BigDecimal.ROUND_HALF_UP);
        DecimalFormat df = new DecimalFormat(
                "#,##0.00",
                new DecimalFormatSymbols(new Locale("es", "UY")));

        return df.format(value.floatValue());
    }
}


