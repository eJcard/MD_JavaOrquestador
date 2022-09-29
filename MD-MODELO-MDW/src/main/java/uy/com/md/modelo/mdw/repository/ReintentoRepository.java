package uy.com.md.modelo.mdw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uy.com.md.modelo.mdw.entity.Reintento;

import java.util.Optional;

public interface ReintentoRepository extends JpaRepository<Reintento, Long> {
    Optional<Reintento> findByReqId(String reqId);
}
