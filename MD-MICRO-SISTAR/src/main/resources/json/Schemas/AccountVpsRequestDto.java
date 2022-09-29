package uy.com.md.sistar.Schemas.json.in;

import lombok.Data;

@Data
public class AccountVpsRequestDto implements Cloneable {

    Long realAccount;
    Boolean excepciones;
    Long id;
    Integer moneda;
    // Indica si se solicita habilitar el parámetro se seguridad (o sea, deshabilitar la excepción de control de limites)
    Boolean enable;
    String inicio;
    String fin;
    Long rrn;
    String producto;
    Long marca;
    Integer emisor;
    String motivo;
    String observaciones;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Boolean getDisable(){
        return !this.enable;
    }
}