package uy.com.md.common.dto;

import lombok.Data;

// Para saltar el error del AM con un request PUT sin body

@Data
public class EmptyBodyDto {
    private String body;
}
