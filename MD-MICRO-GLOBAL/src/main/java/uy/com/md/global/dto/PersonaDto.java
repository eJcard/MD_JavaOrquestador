package uy.com.md.global.dto;

import lombok.Data;
import net.atos.dto.BaseDto;

@Data
public class PersonaDto extends BaseDto {
    private String nombre;
    private String nombre2;
    private String apellido;
    private String apellido2;
    private DocumentoDto doc;
    private String actividad;
    private DireccionDto direccion;
    private String email;
    private Integer nacionalidad;
    private String fechaNacimiento;
    private String sexo;
    private String estadoCivil;
    private TelefonoDto[] telefonos;
    private String relacion;
}
