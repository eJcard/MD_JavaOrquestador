package uy.com.md.modelo.mdw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uy.com.md.modelo.mdw.entity.Operacion;

public interface OperacionRepository extends JpaRepository<Operacion, String> {
}
