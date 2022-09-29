package uy.com.md.persona.repository.rcor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uy.com.md.persona.model.rcor.CodTipoDoc;

@Repository
public interface CodTipoDocRepository  extends JpaRepository<CodTipoDoc, Integer> {
}
