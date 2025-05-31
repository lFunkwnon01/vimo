package com.proyecto_backend.preferenciaNotificacion.domain;


import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.preferenciaNotificacion.dto.PreferenciaNotificacionRequest;
import com.proyecto_backend.preferenciaNotificacion.dto.PreferenciaNotificacionResponse;
import com.proyecto_backend.preferenciaNotificacion.repository.PreferenciaNotificacionRepository;
import com.proyecto_backend.propiedad.Domain.Propiedad;
import com.proyecto_backend.ubicaciones.domain.UbicacionGeografica;
import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreferenciaNotificacionService {

    private final PreferenciaNotificacionRepository preferenciaNotificacionRepository;
    private final UsuarioRepository usuarioRepository;

    // ========== MÉTODOS CRUD EXISTENTES ==========

    public PreferenciaNotificacionResponse crear(PreferenciaNotificacionRequest dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", dto.getUsuarioId()));

        PreferenciaNotificacion preferencia = PreferenciaNotificacion.builder()
                .usuario(usuario)
                .regionInteres(dto.getRegionInteres())
                .distritoInteres(dto.getDistritoInteres())
                .regionUsuario(dto.getRegionUsuario())
                .distritoUsuario(dto.getDistritoUsuario())
                .radioKm(dto.getRadioKm() != null ? dto.getRadioKm() : 10.0)
                .latitudCentro(dto.getLatitudCentro())
                .longitudCentro(dto.getLongitudCentro())
                .tipoBusqueda(dto.getTipoBusqueda() != null ? dto.getTipoBusqueda() : TipoBusqueda.POR_UBICACION)
                .tipo(dto.getTipo())
                .activa(dto.getActiva() != null ? dto.getActiva() : true)
                .build();

        return mapToResponse(preferenciaNotificacionRepository.save(preferencia));
    }

    public PreferenciaNotificacionResponse obtener(Long id) {
        PreferenciaNotificacion p = preferenciaNotificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PreferenciaNotificacion", id));
        return mapToResponse(p);
    }

    public List<PreferenciaNotificacionResponse> listar() {
        return preferenciaNotificacionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PreferenciaNotificacionResponse> listarPorUsuario(Long usuarioId) {
        return preferenciaNotificacionRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PreferenciaNotificacionResponse actualizar(Long id, PreferenciaNotificacionRequest dto) {
        PreferenciaNotificacion p = preferenciaNotificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PreferenciaNotificacion", id));

        if (dto.getRegionInteres() != null) p.setRegionInteres(dto.getRegionInteres());
        if (dto.getDistritoInteres() != null) p.setDistritoInteres(dto.getDistritoInteres());
        if (dto.getRegionUsuario() != null) p.setRegionUsuario(dto.getRegionUsuario());
        if (dto.getDistritoUsuario() != null) p.setDistritoUsuario(dto.getDistritoUsuario());
        if (dto.getRadioKm() != null) p.setRadioKm(dto.getRadioKm());
        if (dto.getLatitudCentro() != null) p.setLatitudCentro(dto.getLatitudCentro());
        if (dto.getLongitudCentro() != null) p.setLongitudCentro(dto.getLongitudCentro());
        if (dto.getTipoBusqueda() != null) p.setTipoBusqueda(dto.getTipoBusqueda());
        if (dto.getTipo() != null) p.setTipo(dto.getTipo());
        if (dto.getActiva() != null) p.setActiva(dto.getActiva());

        return mapToResponse(preferenciaNotificacionRepository.save(p));
    }

    public void eliminar(Long id) {
        if (!preferenciaNotificacionRepository.existsById(id))
            throw new ResourceNotFoundException("PreferenciaNotificacion", id);
        preferenciaNotificacionRepository.deleteById(id);
    }

    // ========== NUEVOS MÉTODOS GEOGRÁFICOS ==========

    /**
     * MÉTODO PRINCIPAL: Busca usuarios interesados en una propiedad específica
     * Este es el que usa PublicacionEventListener
     */
    public List<PreferenciaNotificacion> buscarUsuariosInteresados(Propiedad propiedad, TipoTransaccion tipoTransaccion) {
        UbicacionGeografica ubicacion = propiedad.getUbicacion();

        if (ubicacion == null) {
            log.warn("Propiedad {} no tiene ubicación", propiedad.getId());
            return List.of();
        }

        List<PreferenciaNotificacion> usuariosInteresados = new ArrayList<>();

        // 1. Buscar por ubicación específica (caso Huancayo → Lima)
        usuariosInteresados.addAll(buscarPorUbicacionEspecifica(ubicacion, tipoTransaccion));

        // 2. Buscar por proximidad (caso local)
        usuariosInteresados.addAll(buscarPorProximidadManual(ubicacion, tipoTransaccion));

        return usuariosInteresados.stream().distinct().collect(Collectors.toList());
    }

    public List<PreferenciaNotificacionResponse> buscarPorRegionInteres(String region, TipoTransaccion tipo) {
        return preferenciaNotificacionRepository.findByRegionInteresAndTipoAndActivaTrue(region, tipo)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PreferenciaNotificacionResponse> buscarPorDistritoInteres(String distrito, TipoTransaccion tipo) {
        return preferenciaNotificacionRepository.findByDistritoInteresAndTipoAndActivaTrue(distrito, tipo)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PreferenciaNotificacionResponse> buscarPorTipoBusqueda(TipoBusqueda tipoBusqueda, TipoTransaccion tipo) {
        return preferenciaNotificacionRepository.findByTipoBusquedaAndTipoAndActivaTrue(tipoBusqueda, tipo)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ========== MÉTODOS PRIVADOS PARA LÓGICA GEOGRÁFICA ==========

    private List<PreferenciaNotificacion> buscarPorUbicacionEspecifica(UbicacionGeografica ubicacion, TipoTransaccion tipo) {
        List<PreferenciaNotificacion> resultado = new ArrayList<>();

        if (ubicacion.getRegion() != null) {
            resultado.addAll(preferenciaNotificacionRepository.findByRegionInteresAndTipoAndActivaTrue(ubicacion.getRegion(), tipo));
        }

        if (ubicacion.getDistrito() != null) {
            resultado.addAll(preferenciaNotificacionRepository.findByDistritoInteresAndTipoAndActivaTrue(ubicacion.getDistrito(), tipo));
            resultado.addAll(preferenciaNotificacionRepository.findByDistritoInteresContainingAndTipoAndActivaTrue(ubicacion.getDistrito(), tipo));
        }

        return resultado;
    }

    private List<PreferenciaNotificacion> buscarPorProximidadManual(UbicacionGeografica ubicacion, TipoTransaccion tipo) {
        if (ubicacion.getLatitud() == null || ubicacion.getLongitud() == null) {
            return List.of();
        }

        List<PreferenciaNotificacion> preferenciasConCoordenadas = preferenciaNotificacionRepository
                .findByLatitudCentroIsNotNullAndLongitudCentroIsNotNullAndActivaTrue();

        return preferenciasConCoordenadas.stream()
                .filter(pref -> pref.getTipo().equals(tipo))
                .filter(pref -> pref.getTipoBusqueda() == TipoBusqueda.POR_PROXIMIDAD ||
                        pref.getTipoBusqueda() == TipoBusqueda.AMBAS)
                .filter(pref -> calcularDistancia(
                        ubicacion.getLatitud(), ubicacion.getLongitud(),
                        pref.getLatitudCentro(), pref.getLongitudCentro()
                ) <= pref.getRadioKm())
                .collect(Collectors.toList());
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int RADIO_TIERRA_KM = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIO_TIERRA_KM * c;
    }

    private PreferenciaNotificacionResponse mapToResponse(PreferenciaNotificacion p) {
        return PreferenciaNotificacionResponse.builder()
                .id(p.getId())
                .usuarioId(p.getUsuario().getId())
                .regionInteres(p.getRegionInteres())
                .distritoInteres(p.getDistritoInteres())
                .regionUsuario(p.getRegionUsuario())
                .distritoUsuario(p.getDistritoUsuario())
                .radioKm(p.getRadioKm())
                .latitudCentro(p.getLatitudCentro())
                .longitudCentro(p.getLongitudCentro())
                .tipoBusqueda(p.getTipoBusqueda())
                .tipo(p.getTipo())
                .activa(p.getActiva())
                .build();
    }
}