package uy.com.md.persona.repository.rcor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uy.com.md.persona.model.rcor.Persona;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {
	
	@Query(value="SELECT p "
			+ "FROM Persona p "
			+ "WHERE p.codPaisDocId = :codPaisDocId AND p.codTipoDocId = :codTipoDocId and p.numDoc = :numDoc")
	Optional<Persona> obtPersonaPorDocumento(@Param("codPaisDocId") Integer codPaisDocId, @Param("codTipoDocId") Integer codTipoDocId, @Param("numDoc") String numDoc);
	
}
