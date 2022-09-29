package uy.com.md.mensajes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uy.com.md.mensajes.model.CodMensajeNegocioEntity;

@Repository
public interface CodMensajeNegocioRepository extends JpaRepository<CodMensajeNegocioEntity, String> {

	Optional<CodMensajeNegocioEntity> findFirstByCodigoNegocio(String codigoNegocio);
}
