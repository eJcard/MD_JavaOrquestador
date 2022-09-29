package uy.com.md.mensajes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uy.com.md.mensajes.model.*;

@Repository
public interface CodMensajeExternoRepository extends JpaRepository<CodMensajeExternoEntity, String> {

	Optional<CodMensajeExternoEntity> findFirstByOrigenAndCodigo(String origen, String codigo);

	Optional<CodMensajeExternoEntity> findFirstByCodMensajeNegocio_CodigoNegocio(String codigoNegocio);
}
