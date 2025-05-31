package com.proyecto_backend.reservaVisita.Domain;

import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.eventos.domain.ReservaVisitaCreadaEvent;
import com.proyecto_backend.eventos.domain.VisitaConfirmadaEvent;
import com.proyecto_backend.propiedad.Domain.Propiedad;
import com.proyecto_backend.propiedad.Repository.PropiedadRepository;
import com.proyecto_backend.reservaVisita.Dto.ReservaVisitaRequest;
import com.proyecto_backend.reservaVisita.Dto.ReservaVisitaResponse;
import com.proyecto_backend.reservaVisita.Repository.ReservaVisitaRepository;
import com.proyecto_backend.usuario.Domain.Roles;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaVisitaService {

    private final ReservaVisitaRepository reservaVisitaRepository;
    private final PropiedadRepository propiedadRepository;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReservaVisitaResponse crear(ReservaVisitaRequest dto) {
        Propiedad propiedad = propiedadRepository.findById(dto.getPropiedadId())
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", dto.getPropiedadId()));

        Usuario cliente = usuarioRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (cliente)", dto.getClienteId()));

        Usuario agente = usuarioRepository.findById(dto.getAgenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (agente)", dto.getAgenteId()));

        if (cliente.getRol() != Roles.CLIENTE) {
            throw new IllegalArgumentException("Solo usuarios con rol CLIENTE pueden reservar visitas.");
        }
        if (agente.getRol() != Roles.AGENTE) {
            throw new IllegalArgumentException("Solo usuarios con rol AGENTE pueden ser asignados como agente.");
        }

        ReservaVisita reserva = mapToEntity(dto, propiedad, cliente, agente);
        ReservaVisita reservaGuardada = reservaVisitaRepository.save(reserva);

        log.info("📅 Nueva reserva creada ID: {} - Cliente: {} - Agente: {} - Propiedad: {}",
                reservaGuardada.getId(), cliente.getEmail(), agente.getEmail(), propiedad.getId());

        // Disparar evento de reserva creada
        eventPublisher.publishEvent(new ReservaVisitaCreadaEvent(this, reservaGuardada));

        return mapToResponse(reservaGuardada);
    }

    public ReservaVisitaResponse obtener(Long id) {
        ReservaVisita r = reservaVisitaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReservaVisita", id));
        return mapToResponse(r);
    }

    public List<ReservaVisitaResponse> listar() {
        return reservaVisitaRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReservaVisitaResponse actualizar(Long id, ReservaVisitaRequest dto) {
        ReservaVisita r = reservaVisitaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReservaVisita", id));

        Propiedad propiedad = propiedadRepository.findById(dto.getPropiedadId())
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", dto.getPropiedadId()));

        Usuario cliente = usuarioRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (cliente)", dto.getClienteId()));

        Usuario agente = usuarioRepository.findById(dto.getAgenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (agente)", dto.getAgenteId()));

        r.setPropiedad(propiedad);
        r.setCliente(cliente);
        r.setAgente(agente);
        r.setFechaHora(dto.getFechaHora());
        r.setEstado(dto.getEstado());
        r.setComentarios(dto.getComentarios());

        ReservaVisita reservaActualizada = reservaVisitaRepository.save(r);
        log.info("🔄 Reserva actualizada ID: {}", id);

        return mapToResponse(reservaActualizada);
    }

    public void eliminar(Long id) {
        if (!reservaVisitaRepository.existsById(id))
            throw new ResourceNotFoundException("ReservaVisita", id);
        reservaVisitaRepository.deleteById(id);
        log.info("🗑️ Reserva eliminada ID: {}", id);
    }

    // Métodos personalizados

    public List<ReservaVisitaResponse> listarPorPropiedad(Long propiedadId) {
        return reservaVisitaRepository.findByPropiedadId(propiedadId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReservaVisitaResponse> listarPorCliente(Long clienteId) {
        return reservaVisitaRepository.findByClienteId(clienteId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReservaVisitaResponse> listarPorAgente(Long agenteId) {
        return reservaVisitaRepository.findByAgenteId(agenteId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReservaVisitaResponse> listarPorPropiedadYFecha(Long propiedadId, java.time.LocalDate fecha) {
        return reservaVisitaRepository.findByPropiedadIdAndFechaHoraBetween(
                propiedadId,
                fecha.atStartOfDay(),
                fecha.plusDays(1).atStartOfDay().minusSeconds(1)
        ).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ReservaVisitaResponse confirmarAsistencia(Long id) {
        ReservaVisita reserva = reservaVisitaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReservaVisita", id));

        reserva.setEstado(EstadoReservaVisita.REALIZADA);
        ReservaVisita reservaConfirmada = reservaVisitaRepository.save(reserva);

        log.info("✅ Visita confirmada para reserva ID: {} - Cliente: {}",
                id, reserva.getCliente().getEmail());

        // Publicar el evento de visita confirmada
        eventPublisher.publishEvent(new VisitaConfirmadaEvent(this, reservaConfirmada));

        return mapToResponse(reservaConfirmada);
    }

    // Mapeos

    private ReservaVisita mapToEntity(ReservaVisitaRequest d, Propiedad propiedad, Usuario cliente, Usuario agente) {
        return ReservaVisita.builder()
                .propiedad(propiedad)
                .cliente(cliente)
                .agente(agente)
                .fechaHora(d.getFechaHora())
                .estado(d.getEstado() != null ? d.getEstado() : EstadoReservaVisita.PENDIENTE)
                .comentarios(d.getComentarios())
                .build();
    }

    private ReservaVisitaResponse mapToResponse(ReservaVisita r) {
        return ReservaVisitaResponse.builder()
                .id(r.getId())
                .propiedadId(r.getPropiedad().getId())
                .clienteId(r.getCliente().getId())
                .agenteId(r.getAgente().getId())
                .fechaHora(r.getFechaHora())
                .estado(r.getEstado())
                .comentarios(r.getComentarios())
                .creadoEn(r.getCreadoEn())
                .build();
    }
}