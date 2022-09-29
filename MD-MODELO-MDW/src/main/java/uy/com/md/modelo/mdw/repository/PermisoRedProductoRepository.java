package uy.com.md.modelo.mdw.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;
import uy.com.md.modelo.mdw.entity.PermisoRedProducto;

import java.util.List;

public interface PermisoRedProductoRepository extends JpaRepositoryImplementation<PermisoRedProducto, Long> {

	@Query("select pr "
			+ "from PermisoRedProducto pr "
			+ "where pr.codigoProducto = :codigoProducto "
			+ "and (:codigoGrupoAfinidad is null or (pr.codigoGrupoAfinidad = :codigoGrupoAfinidad or pr.codigoGrupoAfinidad is null)) "
			+ "and pr.red.id = :redId "
			+ "and pr.red.fbaja is NULL "
			+ "and pr.fbaja is NULL")
    List<PermisoRedProducto>  findByProductoGafRed(
    		@Param("codigoProducto") String codigoProducto, 
    		@Param("codigoGrupoAfinidad") String codigoGrupoAfinidad, 
    		@Param("redId") Long redId
    );

}
