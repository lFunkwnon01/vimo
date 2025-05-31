package com.proyecto_backend.solicitudverificacion.domain;

import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.eventos.domain.VerificacionSolicitadaEvent;
import com.proyecto_backend.eventos.service.EmailService;
import com.proyecto_backend.solicitudverificacion.dto.SolicitudVerificacionRequest;
import com.proyecto_backend.solicitudverificacion.repository.SolicitudVerificacionRepository;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificacionService {

    private final UsuarioRepository usuarioRepository;
    private final SolicitudVerificacionRepository solicitudRepository;
    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;

    public SolicitudVerificacion crearSolicitud(SolicitudVerificacionRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", request.getUsuarioId()));

        SolicitudVerificacion solicitud = SolicitudVerificacion.builder()
                .usuario(usuario)
                .documentoAdjunto(request.getDocumentoAdjunto())
                .comentarios(request.getComentarios())
                .estado(EstadoSolicitud.PENDIENTE)
                .build();

        SolicitudVerificacion guardada = solicitudRepository.save(solicitud);

        eventPublisher.publishEvent(new VerificacionSolicitadaEvent(this, usuario));

        return guardada;
    }

    public SolicitudVerificacion actualizarEstado(Long solicitudId, EstadoSolicitud estado, Long adminId, String comentarios) {
        SolicitudVerificacion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("SolicitudVerificacion", solicitudId));

        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));

        solicitud.setEstado(estado);
        solicitud.setAdminAprobador(admin);
        solicitud.setComentarios(comentarios);
        solicitud.setFechaAprobacion(LocalDateTime.now());

        SolicitudVerificacion actualizada = solicitudRepository.save(solicitud);

        emailService.enviarCorreoResultadoVerificacion(solicitud.getUsuario(), estado == EstadoSolicitud.APROBADA);

        return actualizada;
    }
}