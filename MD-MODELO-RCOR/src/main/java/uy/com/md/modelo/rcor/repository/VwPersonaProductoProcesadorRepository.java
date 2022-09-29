package uy.com.md.modelo.rcor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uy.com.md.modelo.rcor.entity.VwPersonaProductoProcesador;

@Repository
public interface VwPersonaProductoProcesadorRepository extends JpaRepository<VwPersonaProductoProcesador, Integer> {

	List<VwPersonaProductoProcesador> findAllByCodPaisDocIdAndCodTipoDocIdAndNumDoc(Integer codPaisDocId, Integer codTipoDocId, String numDoc);

	@Query(nativeQuery = true, value = "SELECT * FROM public.vw_persona_producto_procesador where CONCAT(cod_pais_doc_id, '-', cod_tipo_doc_id, '-', num_doc) IN (:docs)")
	List<VwPersonaProductoProcesador> findAllByDocumentos(@Param("docs") List<String> docs);

	@Query("SELECT v FROM VwPersonaProductoProcesador v WHERE v.numeroCuenta IN (:cuentas)")
	List<VwPersonaProductoProcesador> findAllByCuentas(@Param("cuentas") List<String> cuentas);

	@Query(nativeQuery = true, value = "SELECT * FROM public.vw_persona_producto_procesador where CONCAT(cod_pais_doc_id, '-', cod_tipo_doc_id, '-', num_doc) IN(:docs) and numero_cuenta IN (:cuentas)")
	List<VwPersonaProductoProcesador> findAllByDocumentosAndCuentas(@Param("docs") List<String> docs, @Param("cuentas") List<String> cuentas);
}
