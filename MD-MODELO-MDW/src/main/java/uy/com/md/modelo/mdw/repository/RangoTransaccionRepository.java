package uy.com.md.modelo.mdw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uy.com.md.modelo.mdw.entity.RangoTransaccion;

import java.math.BigDecimal;
import java.util.Optional;

public interface RangoTransaccionRepository extends JpaRepository<RangoTransaccion, Long> {

    Optional<RangoTransaccion> findByAgenteExternoIdAndCodMonedaId(long agenteExterno, int codMonedaId);

    Optional<RangoTransaccion> findByAgenteExternoIdAndCodMonedaIdAndMinimoLessThanEqualAndMaximoGreaterThanEqual(long agenteExternoId, int codMonedaId, BigDecimal minimo, BigDecimal maximo);
}
