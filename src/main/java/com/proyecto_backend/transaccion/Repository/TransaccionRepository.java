package com.proyecto_backend.transaccion.Repository;



import com.proyecto_backend.transaccion.Domain.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByClienteId(Long clienteId);

    List<Transaccion> findByAgenteId(Long agenteId);

    List<Transaccion> findByPropiedadId(Long propiedadId);

    // Para obtener transacciones de un agente en un mes específico
    List<Transaccion> findByAgenteIdAndFechaBetween(Long agenteId, LocalDateTime desde, LocalDateTime hasta);
}
