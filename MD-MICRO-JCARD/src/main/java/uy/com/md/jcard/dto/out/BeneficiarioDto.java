
package uy.com.md.jcard.dto.out;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.atos.dto.BaseDto;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BeneficiarioDto {
    private DocDto doc;
    private String nombre;
    private String nombre2;
    private String apellido;
    private String apellido2;
    private String fechaNacimiento;
    private String sexo;
    private String estadoCivil;
    private String nacionalidad;
    private String email;
    private String actividad;
    private DireccionDto direccion;
    private List<TelefonoDto> telefonos;
}
