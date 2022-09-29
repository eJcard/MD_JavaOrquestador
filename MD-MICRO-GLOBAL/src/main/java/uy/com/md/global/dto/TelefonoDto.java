package uy.com.md.global.dto;

import lombok.Data;
import net.atos.dto.BaseDto;

@Data
public class TelefonoDto extends BaseDto {
    private Integer tipoTel;
    private String num;
}
