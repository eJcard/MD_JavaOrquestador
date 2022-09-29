package uy.com.md.persona.repository.rcor;

import org.springframework.data.jpa.repository.JpaRepository;
import uy.com.md.persona.model.rcor.Direccion;

import java.util.Optional;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    Optional<Direccion> findByPersonaIdAndOrigenId(Integer personaId, String origenId);
}
