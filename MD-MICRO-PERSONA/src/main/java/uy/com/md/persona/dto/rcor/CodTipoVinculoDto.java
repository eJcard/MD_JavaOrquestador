package uy.com.md.persona.dto.rcor;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
public class CodTipoVinculoDto {
    private Integer id;
    private String nombre;
}
