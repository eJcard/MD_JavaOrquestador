package uy.com.md.jcard.dto.in;

import lombok.Data;
import net.atos.dto.BaseDto;

@Data
public class BeneficiarioDto{
    private String tipoDoc;
    private String pais;
    private String doc;
}
