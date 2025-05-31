package com.proyecto_backend.eventos.domain;

import com.proyecto_backend.eventos.domain.TransaccionCompletadaEvent;
import com.proyecto_backend.eventos.service.EmailService;
import com.proyecto_backend.transaccion.Domain.Transaccion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransaccionCompletadaEventListener {

    private final EmailService emailService;

    @EventListener
    public void handleTransaccionCompletada(TransaccionCompletadaEvent event) {
        Transaccion transaccion = event.getTransaccion();
        log.info("Evento TransaccionCompletada recibido para transacción ID: {}", transaccion.getId());
        emailService.enviarCorreoConfirmacionTransaccion(transaccion);
    }
}