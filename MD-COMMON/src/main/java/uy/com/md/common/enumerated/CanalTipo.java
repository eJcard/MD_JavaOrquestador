package uy.com.md.common.enumerated;

public enum CanalTipo {
    BANCO("banco"), OTROS_PROCESADORES("otrosProcesadores"), MIDINERO("midinero"),  CREDITOS("creditos"), TRANSFERENCIA_BANCO("transferenciaBanco");

    public final String value;

    CanalTipo(String value) {
        this.value = value;
    }
}
