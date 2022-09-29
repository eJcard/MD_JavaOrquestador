package uy.com.md.common.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class Auditable {

    @Column(length = 50, nullable = false)
    private String uact;

    /*@Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime fcreacion;*/

    @Column
    @UpdateTimestamp
    private LocalDateTime fact;

    // private LocalDateTime fbaja;

    @Column(name = "corr_id", length = 50)
    private String corrId;
}
