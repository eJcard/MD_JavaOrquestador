package uy.com.md.modelo.rcor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uy.com.md.modelo.rcor.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
