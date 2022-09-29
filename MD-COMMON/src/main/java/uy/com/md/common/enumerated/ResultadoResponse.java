package uy.com.md.common.enumerated;

@Deprecated
public enum ResultadoResponse {
    TRUE(true), FALSE(false);

    private boolean code;

    ResultadoResponse(boolean code) {
        this.code = code;
    }
}
