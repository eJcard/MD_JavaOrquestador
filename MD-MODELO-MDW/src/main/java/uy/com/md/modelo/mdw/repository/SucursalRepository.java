package uy.com.md.modelo.mdw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uy.com.md.modelo.mdw.entity.Sucursal;

import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    Optional<Sucursal> findByCodigoAndRedId(String codigo, Long red);
}
