package uy.com.md.modelo.mdw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uy.com.md.modelo.mdw.entity.VwPersonaProducto;

import java.util.Optional;

public interface VwPersonaProductoRepository extends JpaRepository<VwPersonaProducto, String> {
    Optional<VwPersonaProducto> findByCodPaisDocIdAndCodTipoDocIdAndNumDocAndNumeroCuenta(Integer codPaisDocId, Integer codTipoDocId, String numDoc, String numeroCuenta);
}
