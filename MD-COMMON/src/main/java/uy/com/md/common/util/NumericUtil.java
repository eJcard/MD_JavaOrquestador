package uy.com.md.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumericUtil {

    public static BigDecimal formatBigDecimal(BigDecimal value, int places) {    	   	
    	return value.setScale(places, RoundingMode.HALF_UP);
    }
}
