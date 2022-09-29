package uy.com.md.persona.repository.rcor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uy.com.md.persona.model.rcor.Contacto;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Integer> {

    Optional<Contacto> findByPersonaIdAndOrigenIdAndEtiqueta(Integer personaId, String origenId, String etiqueta);

    List<Contacto> findByPersonaIdAndOrigenId(Integer personaId, String origenId);

    Optional<Contacto> findByIdAndOrigenId(Integer id, String origenId);
}
