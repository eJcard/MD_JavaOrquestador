package uy.com.md.persona.model.rcor;

import lombok.Data;

import java.io.Serializable;

@Data
public class PersonaId implements Serializable {

    private Integer personaId;
    private String origenId;
}
