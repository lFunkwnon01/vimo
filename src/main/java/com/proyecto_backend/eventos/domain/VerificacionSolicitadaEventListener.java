package com.proyecto_backend.eventos.domain;

import com.proyecto_backend.eventos.domain.VerificacionSolicitadaEvent;
import com.proyecto_backend.eventos.service.EmailService;
import com.proyecto_backend.usuario.Domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerificacionSolicitadaEventListener {

    private final EmailService emailService;

    @EventListener
    public void handleVerificacionSolicitada(VerificacionSolicitadaEvent event) {
        Usuario usuario = event.getUsuario();
        log.info("Evento VerificacionSolicitada recibido para usuario ID: {}", usuario.getId());
        emailService.enviarCorreoNotificacionVerificacionAdmin(usuario);
    }
}