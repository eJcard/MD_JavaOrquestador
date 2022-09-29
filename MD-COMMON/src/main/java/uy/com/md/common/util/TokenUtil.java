package uy.com.md.common.util;

import static uy.com.md.common.util.Constantes.AUTH_TOKEN_TYPE_BEARER;

public class TokenUtil {

    public static final String BEARER = "Bearer";

    private TokenUtil() {
        throw new IllegalStateException("TokenUtil utility class");
    }

    public static String buildToken(String token) {
        return String.format("%1$s%2$s", AUTH_TOKEN_TYPE_BEARER, token);
    }

    public static String getToken(String token) {
        String tokenValue = token.trim();
        if (tokenValue.contains(BEARER)) {
            tokenValue = tokenValue.split(BEARER)[1].trim();
        }
        return tokenValue;
    }
}
