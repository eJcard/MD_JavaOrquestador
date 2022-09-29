package uy.com.md.sistar.dto.out;

import java.util.List;

import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import uy.com.md.sistar.util.SistarConstants;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BeneficiarioDto {

   private DocDto doc;

   @Schema(example = "Martín")
   private String nombre;
   
   @Schema(example = "José")
   private String nombre2;
   
   @Schema(example = "Fernandez")
   private String apellido;
   
   @Schema(example = "Gomez")
   private String apellido2;
   
   @Schema(example = "1980-01-01")
   private String fechaNacimiento;
   
   @Schema(example = "M", allowableValues = {"M", "F"})
   private String sexo;
   
   @Schema(example = "C", allowableValues = {"S", "C", "V", "D"})
   private String estadoCivil;

   @Pattern(regexp = SistarConstants.COUNTRY_CODE_PATTERN)
   @Schema(example = "858", description = SistarConstants.COUNTRY_CODE_DESCRIPTION)
   private String nacionalidad;
   
   
   @Pattern(regexp = SistarConstants.EMAIL_PATTERN)
   private String email;

   @Schema(description = "Activity code", minLength = 8, maxLength = 8)
   @Pattern(regexp = "\\d{8}")
   private String actividad;
   
   private DireccionDto direccion;

   private List<TelefonoDto> telefonos = null;

   @Schema(description = "Relationship between beneficiary and representative")
   private String relacion;
}
