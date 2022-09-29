package uy.com.md.persona.dto.rcor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import net.atos.dto.BaseDto;
import uy.com.md.persona.util.LocalDateTimeDeserializer;
import uy.com.md.persona.util.LocalDateTimeSerializer;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonaJuridicaDto extends BaseDto{
    private Integer personaId;
    private String razonSocial;
    private String nombreFantasia;
    private String uact;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)  
    @JsonSerialize(using = LocalDateTimeSerializer.class)  
    private LocalDateTime fact;
    private String origenId;
    private Integer numBps;
    private Integer codTipoEmpresaId;
    private Integer codActividadEconomicaId;
    private DireccionDto direccion;
}
