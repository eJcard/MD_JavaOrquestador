package uy.com.md.modelo.mdw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uy.com.md.modelo.mdw.entity.Idempotencia;

import java.util.List;
import java.util.Optional;

public interface IdempotenciaRepository extends JpaRepository<Idempotencia, Long> {

    Optional<Idempotencia> findBySlugAndEstadoNotIn(String slug, List<String> states);

    Optional<Idempotencia> findByReqId(String correlationId);

    Optional<Idempotencia> findByReqIdAndEstadoNotIn(String correlationId, List<String> states);
}
