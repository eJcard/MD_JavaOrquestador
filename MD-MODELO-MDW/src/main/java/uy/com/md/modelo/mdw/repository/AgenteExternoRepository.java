package uy.com.md.modelo.mdw.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import uy.com.md.modelo.mdw.entity.AgenteExterno;

import java.util.Optional;

public interface AgenteExternoRepository extends JpaRepositoryImplementation<AgenteExterno, Long> {

    Optional<AgenteExterno> findByClientId(String clientId);

    Optional<AgenteExterno> findByCodPaisDocIdAndCodTipoDocIdAndNumDoc(int codPaisDocId, int codTipoDocId, String numDoc);
}
