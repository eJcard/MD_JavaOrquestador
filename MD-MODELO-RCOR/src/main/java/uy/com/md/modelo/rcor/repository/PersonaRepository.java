package uy.com.md.modelo.rcor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uy.com.md.modelo.rcor.entity.Persona;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {
	Persona findByCodPaisDocIdAndCodTipoDocIdAndNumDoc(Integer codPaisDocId, Integer codTipoDocId, String numDoc);
}
