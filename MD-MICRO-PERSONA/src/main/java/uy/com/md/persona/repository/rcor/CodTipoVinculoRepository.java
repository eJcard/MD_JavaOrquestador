package uy.com.md.persona.repository.rcor;

import org.springframework.data.jpa.repository.JpaRepository;
import uy.com.md.persona.model.rcor.CodTipoVinculo;

import java.util.Optional;

public interface CodTipoVinculoRepository extends JpaRepository<CodTipoVinculo, Integer> {
    Optional<CodTipoVinculo> findByNombreIgnoreCase(String nombre);
}
