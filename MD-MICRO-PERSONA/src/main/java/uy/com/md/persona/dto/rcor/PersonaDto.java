package uy.com.md.persona.dto.rcor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import net.atos.dto.BaseDto;
import uy.com.md.persona.util.LocalDateTimeDeserializer;
import uy.com.md.persona.util.LocalDateTimeSerializer;

import javax.persistence.Column;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonaDto extends BaseDto {
    private Integer id;
    private Integer codPaisDocId;
    private Integer codTipoDocId;
    private String numDoc;
    private String denominacion;
    private Boolean personaActiva;
    private String ucreacion;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)  
    @JsonSerialize(using = LocalDateTimeSerializer.class)  
    private LocalDateTime fcreacion;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)  
    @JsonSerialize(using = LocalDateTimeSerializer.class)  
    private LocalDateTime fact;
    private Integer codTipoPersonaId;
    private List<ContactoDto> contactos;
    private PersonaFisicaDto personaFisicaDto;
    private PersonaJuridicaDto personaJuridicaDto;
}
