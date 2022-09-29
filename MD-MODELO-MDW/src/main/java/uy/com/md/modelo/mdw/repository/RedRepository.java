package uy.com.md.modelo.mdw.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import uy.com.md.modelo.mdw.entity.AgenteExterno;
import uy.com.md.modelo.mdw.entity.Red;

import java.util.Optional;

public interface RedRepository extends JpaRepositoryImplementation<Red, Long> {

    Optional<Red> findByAgenteExterno(AgenteExterno agenteExterno);

}
