package uy.com.md.persona.repository.rcor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uy.com.md.persona.model.rcor.PersonaId;
import uy.com.md.persona.model.rcor.PersonaJuridica;

import java.util.Optional;

@Repository
public interface PersonaJuridicaRepository extends JpaRepository<PersonaJuridica, PersonaId> {
    Optional<PersonaJuridica> findByPersonaIdAndOrigenId(Integer personaId, String origenId);
}
