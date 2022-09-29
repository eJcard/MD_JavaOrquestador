package uy.com.md.sistar.dto.in;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import uy.com.md.sistar.util.SistarConstants;

@Data
public class ReverseRequestDto {

   String canal;
   
   @Schema(required = true)
   private Integer emisor;
   
   @Schema(required = true)
   @Size(min = 1, max = 8)
   @Pattern(regexp = SistarConstants.RRN_PATTERN)
   private String rrn;

   @Schema(required = true)
   @Size(min = 1, max = 8)
   @Pattern(regexp = SistarConstants.RRN_PATTERN)
   private String toReverseRrn;

   private String cuenta;
   private Integer moneda;
   private Integer sucursal;
   private Boolean reversaRecarga = true;
}
