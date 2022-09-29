package uy.com.md.jcard.dto.in;

import lombok.Data;
import net.atos.dto.BaseDto;

@Data
public class EmpresaDto{
    private String tipoDoc;
    private String pais;
    private String doc;
    private String nombre;
}
