package uy.com.md.persona.model.rcor;

import lombok.Data;
import net.atos.dto.BaseDto;
import uy.com.md.common.model.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
public class Origen extends Auditable implements Serializable {

    private static final long serialVersionUID = 637074694082165784L;

    @Id
    @Column(name = "origen_id", length = 100)
    private String id;
}
