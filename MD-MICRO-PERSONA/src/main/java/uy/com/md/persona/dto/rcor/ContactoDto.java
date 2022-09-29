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
public class ContactoDto extends BaseDto {
    private Integer id;
    private Integer personaId;
    private Integer codTipoContactoId;
    private String etiqueta;
    private String contacto;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)  
    @JsonSerialize(using = LocalDateTimeSerializer.class)  
    private LocalDateTime fact;
    private String origen;
}
