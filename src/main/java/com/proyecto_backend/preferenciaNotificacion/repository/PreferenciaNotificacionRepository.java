package com.proyecto_backend.preferenciaNotificacion.repository;


import com.proyecto_backend.preferenciaNotificacion.domain.PreferenciaNotificacion;
import com.proyecto_backend.preferenciaNotificacion.domain.TipoBusqueda;
import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferenciaNotificacionRepository extends JpaRepository<PreferenciaNotificacion, Long> {
    // MÉTODOS BÁSICOS CON JPA QUERY METHODS
    List<PreferenciaNotificacion> findByUsuarioId(Long usuarioId);

    List<PreferenciaNotificacion> findByActivaTrue();

    List<PreferenciaNotificacion> findByTipoAndActivaTrue(TipoTransaccion tipo);

    List<PreferenciaNotificacion> findByRegionInteresAndTipoAndActivaTrue(String regionInteres, TipoTransaccion tipo);

    List<PreferenciaNotificacion> findByDistritoInteresAndTipoAndActivaTrue(String distritoInteres, TipoTransaccion tipo);

    List<PreferenciaNotificacion> findByTipoBusquedaAndActivaTrue(TipoBusqueda tipoBusqueda);

    List<PreferenciaNotificacion> findByTipoBusquedaAndTipoAndActivaTrue(TipoBusqueda tipoBusqueda, TipoTransaccion tipo);

    List<PreferenciaNotificacion> findByDistritoInteresContainingAndTipoAndActivaTrue(String distrito, TipoTransaccion tipo);

    List<PreferenciaNotificacion> findByLatitudCentroIsNotNullAndLongitudCentroIsNotNullAndActivaTrue();
}