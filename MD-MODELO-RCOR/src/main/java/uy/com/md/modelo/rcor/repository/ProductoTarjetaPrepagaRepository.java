package uy.com.md.modelo.rcor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uy.com.md.modelo.rcor.entity.ProductoTarjetaPrepaga;

import java.util.Optional;

@Repository
public interface ProductoTarjetaPrepagaRepository extends JpaRepository<ProductoTarjetaPrepaga, Integer> {
    Optional<ProductoTarjetaPrepaga> findByProductoIdAndProcesadorId(Integer productoId, Integer procesadorId);
}
