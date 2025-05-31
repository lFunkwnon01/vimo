package com.proyecto_backend.publicacion.Domain;

import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.eventos.domain.PublicacionEvent;
import com.proyecto_backend.propiedad.Domain.Propiedad;
import com.proyecto_backend.propiedad.Repository.PropiedadRepository;
import com.proyecto_backend.publicacion.Dto.PublicacionRequest;
import com.proyecto_backend.publicacion.Dto.PublicacionResponse;
import com.proyecto_backend.publicacion.Repository.PublicacionRepository;
import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import com.proyecto_backend.usuario.Domain.Roles;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final PropiedadRepository propiedadRepository;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PublicacionResponse crear(PublicacionRequest dto) {
        Propiedad propiedad = propiedadRepository.findById(dto.getPropiedadId())
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", dto.getPropiedadId()));

        Usuario creador = usuarioRepository.findById(dto.getCreadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (creador)", dto.getCreadorId()));

        // Solo propietario o admin puede crear publicación
        if (!(creador.getRol() == Roles.PROPIETARIO || creador.getRol() == Roles.ADMIN)) {
            throw new IllegalArgumentException("Solo el propietario o un admin puede crear publicaciones.");
        }

        // Solo una publicación activa por propiedad en el rango de fechas
        boolean existeActiva = publicacionRepository.existsByPropiedadIdAndEstadoAndFechaFinAfter(
                dto.getPropiedadId(), EstadoPublicacion.ACTIVA, dto.getFechaInicio().minusDays(1));
        if (existeActiva) {
            throw new IllegalArgumentException("Ya existe una publicación activa para esta propiedad en el rango de fechas.");
        }

        Publicacion publicacion = mapToEntity(dto, propiedad, creador);
        Publicacion publicacionGuardada = publicacionRepository.save(publicacion);

        log.info("📢 Nueva publicación creada ID: {} para propiedad ID: {} - Tipo: {}",
                publicacionGuardada.getId(), propiedad.getId(), publicacionGuardada.getTipoTransaccion());

        // Disparar evento para notificar usuarios interesados
        eventPublisher.publishEvent(new PublicacionEvent(this, propiedad, false));

        return mapToResponse(publicacionGuardada);
    }

    public PublicacionResponse obtener(Long id) {
        Publicacion p = publicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicacion", id));
        return mapToResponse(p);
    }

    public List<PublicacionResponse> listar() {
        return publicacionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PublicacionResponse actualizar(Long id, PublicacionRequest dto) {
        Publicacion p = publicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publicacion", id));

        Propiedad propiedad = propiedadRepository.findById(dto.getPropiedadId())
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", dto.getPropiedadId()));

        Usuario creador = usuarioRepository.findById(dto.getCreadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (creador)", dto.getCreadorId()));

        p.setPropiedad(propiedad);
        p.setCreador(creador);
        p.setEstado(dto.getEstado());
        p.setFechaInicio(dto.getFechaInicio());
        p.setFechaFin(dto.getFechaFin());
        p.setTipoTransaccion(dto.getTipoTransaccion() != null ? dto.getTipoTransaccion() : TipoTransaccion.VENTA);

        Publicacion publicacionActualizada = publicacionRepository.save(p);

        log.info("🔄 Publicación actualizada ID: {} para propiedad ID: {}",
                publicacionActualizada.getId(), propiedad.getId());

        // Disparar evento de actualización
        eventPublisher.publishEvent(new PublicacionEvent(this, propiedad, true));

        return mapToResponse(publicacionActualizada);
    }

    public void eliminar(Long id) {
        if (!publicacionRepository.existsById(id))
            throw new ResourceNotFoundException("Publicacion", id);
        publicacionRepository.deleteById(id);
        log.info("🗑️ Publicación eliminada ID: {}", id);
    }

    // Métodos personalizados

    public List<PublicacionResponse> listarPorPropiedad(Long propiedadId) {
        return publicacionRepository.findByPropiedadId(propiedadId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PublicacionResponse> listarPorAgente(Long agenteId) {
        return publicacionRepository.findByAgentesId(agenteId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PublicacionResponse agregarAgente(Long publicacionId, Long agenteId) {
        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Publicacion", publicacionId));
        Usuario agente = usuarioRepository.findById(agenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (agente)", agenteId));

        if (agente.getRol() != Roles.AGENTE) {
            throw new IllegalArgumentException("Solo usuarios con rol AGENTE pueden postularse.");
        }

        if (!publicacion.getAgentes().contains(agente)) {
            publicacion.getAgentes().add(agente);
            publicacionRepository.save(publicacion);
            log.info("👨‍💼 Agente ID: {} agregado a publicación ID: {}", agenteId, publicacionId);
        }

        return mapToResponse(publicacion);
    }

    public PublicacionResponse removerAgente(Long publicacionId, Long agenteId) {
        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Publicacion", publicacionId));
        Usuario agente = usuarioRepository.findById(agenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (agente)", agenteId));

        publicacion.getAgentes().remove(agente);
        publicacionRepository.save(publicacion);
        log.info("👨‍💼 Agente ID: {} removido de publicación ID: {}", agenteId, publicacionId);

        return mapToResponse(publicacion);
    }

    public PublicacionResponse cerrarPublicacion(Long publicacionId, Long agenteGanadorId) {
        Publicacion publicacion = publicacionRepository.findById(publicacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Publicacion", publicacionId));

        Usuario agenteGanador = usuarioRepository.findById(agenteGanadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Agente", agenteGanadorId));

        // Verificar que el agente esté postulado
        if (!publicacion.getAgentes().contains(agenteGanador)) {
            throw new IllegalArgumentException("El agente no está postulado a esta publicación");
        }

        publicacion.setEstado(EstadoPublicacion.CERRADA);
        Publicacion publicacionCerrada = publicacionRepository.save(publicacion);

        log.info("🏆 Publicación ID: {} cerrada - Agente ganador ID: {}", publicacionId, agenteGanadorId);

        return mapToResponse(publicacionCerrada);
    }

    public PublicacionResponse crearNuevaPublicacionParaMismaPropiedad(Long propiedadId, PublicacionRequest dto) {
        // Cerrar publicaciones activas anteriores
        List<Publicacion> publicacionesActivas = publicacionRepository
                .findByPropiedadIdAndEstado(propiedadId, EstadoPublicacion.ACTIVA);

        for (Publicacion pub : publicacionesActivas) {
            pub.setEstado(EstadoPublicacion.EXPIRADA);
            publicacionRepository.save(pub);
        }

        log.info("📋 Expiradas {} publicaciones activas para crear nueva publicación de propiedad ID: {}",
                publicacionesActivas.size(), propiedadId);

        // Crear nueva publicación
        return crear(dto);
    }

    // Mapeos

    private Publicacion mapToEntity(PublicacionRequest d, Propiedad propiedad, Usuario creador) {
        return Publicacion.builder()
                .propiedad(propiedad)
                .creador(creador)
                .estado(d.getEstado())
                .fechaInicio(d.getFechaInicio())
                .fechaFin(d.getFechaFin())
                .tipoTransaccion(d.getTipoTransaccion() != null ? d.getTipoTransaccion() : TipoTransaccion.VENTA)
                .agentes(new ArrayList<>())
                .build();
    }

    private PublicacionResponse mapToResponse(Publicacion p) {
        return PublicacionResponse.builder()
                .id(p.getId())
                .propiedadId(p.getPropiedad().getId())
                .creadorId(p.getCreador().getId())
                .estado(p.getEstado())
                .fechaInicio(p.getFechaInicio())
                .fechaFin(p.getFechaFin())
                .tipoTransaccion(p.getTipoTransaccion())
                .agentesIds(
                        p.getAgentes() != null ?
                                p.getAgentes().stream().map(Usuario::getId).collect(Collectors.toList())
                                : new ArrayList<>()
                )
                .creadoEn(p.getCreadoEn())
                .build();
    }
}