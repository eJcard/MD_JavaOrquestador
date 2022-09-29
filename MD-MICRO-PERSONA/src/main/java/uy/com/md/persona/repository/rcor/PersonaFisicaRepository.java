package uy.com.md.persona.repository.rcor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uy.com.md.persona.model.rcor.PersonaFisica;
import uy.com.md.persona.model.rcor.PersonaId;

import java.util.Optional;

@Repository
public interface PersonaFisicaRepository extends JpaRepository<PersonaFisica, PersonaId> {
    Optional<PersonaFisica> findByPersonaIdAndOrigenId(Integer personaId, String origen);
}


