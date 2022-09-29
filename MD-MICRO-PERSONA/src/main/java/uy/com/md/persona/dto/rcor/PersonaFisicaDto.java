package uy.com.md.persona.dto.rcor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import net.atos.dto.BaseDto;
import uy.com.md.persona.util.LocalDateDeserializer;
import uy.com.md.persona.util.LocalDateSerializer;
import uy.com.md.persona.util.LocalDateTimeDeserializer;
import uy.com.md.persona.util.LocalDateTimeSerializer;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonaFisicaDto extends BaseDto {
    private Integer personaId;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    @JsonDeserialize(using = LocalDateDeserializer.class)  
    @JsonSerialize(using = LocalDateSerializer.class)  
    private LocalDate fechaNac;
    private String codSexoId;
    private Integer codPaisNacId;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)  
    @JsonSerialize(using = LocalDateTimeSerializer.class)  
    private LocalDateTime fact;
    private Character codEstadoCivilId;
    private Integer actividadEconomicaTipoSectorId;
    private String origenId;
    private DireccionDto direccion;
}
