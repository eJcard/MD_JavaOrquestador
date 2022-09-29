package uy.com.md.jcard.dto.info;

import lombok.Data;

import java.util.List;

@Data
public class ClienteDto {
    private String nombre;
    private String apellido;
    private DocumentoDto doc;
    private String actividad;
    private String email;
    private String fechaNacimiento;
    private String sexo;
    private String estadoCivil;
    private Boolean operaPorTerceros;
    private String nacionalidad;
    private List<TelefonoDto> telefonos;
}
