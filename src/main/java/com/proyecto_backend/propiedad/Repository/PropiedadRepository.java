package com.proyecto_backend.propiedad.Repository;

import com.proyecto_backend.propiedad.Domain.EstadoPropiedad;
import com.proyecto_backend.propiedad.Domain.Propiedad;
import com.proyecto_backend.propiedad.Domain.TipoPropiedad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {
    /// Buscar todas las propiedades de un propietario específico
    List<Propiedad> findByPropietarioId(Long propietarioId);

    // Buscar propiedades por estado (disponible, reservado, etc.)
    List<Propiedad> findByEstado(EstadoPropiedad estado);

    // Buscar propiedades por tipo (departamento, casa, terreno)
    List<Propiedad> findByTipo(TipoPropiedad tipo);

    // Buscar propiedades por propietario y estado
    List<Propiedad> findByPropietarioIdAndEstado(Long propietarioId, EstadoPropiedad estado);

    // Buscar propiedades verificadas
    List<Propiedad> findByVerificadaTrue();

    // Buscar propiedades no verificadas
    List<Propiedad> findByVerificadaFalse();

    // Puedes agregar más según tus necesidades de negocio
}
