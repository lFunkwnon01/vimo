package com.proyecto_backend.reservaVisita.Repository;



import com.proyecto_backend.reservaVisita.Domain.ReservaVisita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaVisitaRepository extends JpaRepository<ReservaVisita, Long> {

    List<ReservaVisita> findByPropiedadId(Long propiedadId);

    List<ReservaVisita> findByClienteId(Long clienteId);

    List<ReservaVisita> findByAgenteId(Long agenteId);

    // Buscar reservas por propiedad y rango de fecha/hora
    List<ReservaVisita> findByPropiedadIdAndFechaHoraBetween(Long propiedadId, LocalDateTime desde, LocalDateTime hasta);
}
