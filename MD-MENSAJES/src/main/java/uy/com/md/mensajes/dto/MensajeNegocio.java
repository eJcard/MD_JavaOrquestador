package uy.com.md.mensajes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uy.com.md.mensajes.interfaces.MensajeNegocioInterface;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeNegocio implements MensajeNegocioInterface {
    private String origen;
    private String codigo;

    @Override
    public String getCodigoNegocio() {
        return this.codigo;
    }
}
