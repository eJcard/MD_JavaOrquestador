package uy.com.md.modelo.mdw.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;
import uy.com.md.modelo.mdw.entity.PermisoRedOperacion;

import java.util.Optional;

public interface PermisoRedOperacionRepository extends JpaRepositoryImplementation<PermisoRedOperacion, Long> {
	
	@Query("select pro "
			+ "from PermisoRedOperacion pro "
			+ "where pro.operacion.id = :operacion "
			+ "and pro.operacion.fbaja is NULL "
			+ "and pro.red.id = :redId "
			+ "and pro.red.fbaja is NULL "
			+ "and pro.fbaja is NULL")
    Optional<PermisoRedOperacion>  findByRedOperacion(
    		@Param("operacion") String operacion, 
    		@Param("redId") Long redId
    );
}
