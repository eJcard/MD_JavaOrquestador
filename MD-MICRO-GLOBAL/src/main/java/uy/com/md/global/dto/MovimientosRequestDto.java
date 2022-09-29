package uy.com.md.global.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.atos.dto.BaseDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientosRequestDto extends BaseDto {
    private Long cuentanumero;
    private Short tipomovimiento;
    private String fechadesde;
    private String fechahasta;
    private Integer paginaid;
    private Short paginatam;
    private Integer filtromoneda;
    private String filtrodescripcion;
    private String filtromcc;
    private Long filtromontodesde;
    private Long filtromontohasta;
    private String filtrocomercio;
    private Long filtrocuentadestino;
    private String otrosfiltros;
    private Short sucursal;
    private String caja;
}
