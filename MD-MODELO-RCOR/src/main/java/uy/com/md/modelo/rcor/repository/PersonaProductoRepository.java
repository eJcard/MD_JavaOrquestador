package uy.com.md.modelo.rcor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uy.com.md.modelo.rcor.entity.PersonaProducto;

@Repository
public interface PersonaProductoRepository extends JpaRepository<PersonaProducto, Integer> {

//	@Query(value="SELECT pp "
//			+ "FROM PersonaProducto pp "
//			+ "WHERE persona.codPaisDocId = :codPaisDocId AND persona.codTipoDocId = :codTipoDocId and persona.numDoc = :numDoc")
//	Optional<Persona> obtPersonaPorDocumento(@Param("codPaisDocId") Integer codPaisDocId, @Param("codTipoDocId") Integer codTipoDocId, @Param("numDoc") String numDoc);
	
	List<PersonaProducto> findByPersona_CodPaisDocIdAndPersona_CodTipoDocIdAndPersona_NumDoc(Integer codPaisDocId, Integer codTipoDocId, String numDoc);
}
