package uy.com.md.persona.repository.rcor;


import org.springframework.data.jpa.repository.JpaRepository;
import uy.com.md.persona.model.rcor.CodTipoVinculo;
import uy.com.md.persona.model.rcor.Persona;
import uy.com.md.persona.model.rcor.Vinculo;

import java.util.List;
import java.util.Optional;

public interface VinculoRepository extends JpaRepository<Vinculo, Long> {
    Optional<Vinculo> findByPersonaIdAAndPersonaIdB(Persona a, Persona b);

    List<Vinculo> findByPersonaIdA(Persona a);

    List<Vinculo> findByPersonaIdAAndCodTipoVinculo(Persona a, CodTipoVinculo codTipoVinculo);

    Optional<Vinculo> findByPersonaIdAAndPersonaIdBAndCodTipoVinculo(Persona a, Persona b, CodTipoVinculo codTipoVincul);

}
