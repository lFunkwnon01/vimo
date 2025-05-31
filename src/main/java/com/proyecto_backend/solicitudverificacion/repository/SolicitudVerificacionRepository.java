package com.proyecto_backend.solicitudverificacion.repository;

import com.proyecto_backend.solicitudverificacion.domain.SolicitudVerificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudVerificacionRepository extends JpaRepository<SolicitudVerificacion, Long> {

    List<SolicitudVerificacion> findByUsuarioId(Long usuarioId);

    List<SolicitudVerificacion> findByEstado(String estado);
}