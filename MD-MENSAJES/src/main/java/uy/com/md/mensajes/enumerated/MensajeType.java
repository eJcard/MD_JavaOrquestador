package uy.com.md.mensajes.enumerated;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum MensajeType {
	INFO("I"), ERROR("E"), WARNING("W"), SUCCESS("S"), NONE("");

	private final String id;

	private MensajeType(String id) {
		this.id = id;
	}

	private static final Map<String, MensajeType> LABELS = new HashMap<>();

	static {
		Arrays.asList(values()).forEach(e -> LABELS.put(e.id, e));
	}

	public String getLabel() {
		return this.id;
	}

	public static MensajeType valueOfLabel(String label) {
		return LABELS.get(label);
	}
}
