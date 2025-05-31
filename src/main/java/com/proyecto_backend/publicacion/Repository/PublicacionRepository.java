package com.proyecto_backend.publicacion.Repository;

import com.proyecto_backend.publicacion.Domain.EstadoPublicacion;
import com.proyecto_backend.publicacion.Domain.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {

    // Buscar publicaciones por propiedad
    List<Publicacion> findByPropiedadId(Long propiedadId);

    // Buscar publicaciones donde un agente está postulado
    List<Publicacion> findByAgentesId(Long agenteId);

    // Verificar si ya existe una publicación activa para una propiedad en un rango de fechas
    boolean existsByPropiedadIdAndEstadoAndFechaFinAfter(Long propiedadId, EstadoPublicacion estado, LocalDate fechaInicio);

    // MÉTODO FALTANTE AGREGADO:
    List<Publicacion> findByPropiedadIdAndEstado(Long propiedadId, EstadoPublicacion estado);
}