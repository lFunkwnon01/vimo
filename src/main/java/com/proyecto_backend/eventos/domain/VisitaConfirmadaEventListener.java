package com.proyecto_backend.eventos.domain;

import com.proyecto_backend.eventos.service.EmailService;
import com.proyecto_backend.reservaVisita.Domain.ReservaVisita;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VisitaConfirmadaEventListener {

    private final EmailService emailService;

    @EventListener
    public void handleVisitaConfirmada(VisitaConfirmadaEvent event) {
        ReservaVisita reserva = event.getReservaVisita();

        log.info("✅ Procesando evento de visita confirmada ID: {} - Cliente: {}",
                reserva.getId(), reserva.getCliente().getEmail());

        try {
            // Enviar correo de agradecimiento al cliente
            emailService.enviarCorreoAgradecimiento(reserva);
            log.info("💌 Correo de agradecimiento enviado a: {}", reserva.getCliente().getEmail());

            // TODO: Enviar encuesta de satisfacción (implementar después)
            // emailService.enviarEncuestaSatisfaccion(reserva);

        } catch (Exception e) {
            log.error("❌ Error procesando evento de visita confirmada ID: {} - Error: {}",
                    reserva.getId(), e.getMessage(), e);
        }
    }
}