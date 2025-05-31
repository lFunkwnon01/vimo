package com.proyecto_backend.eventos.domain;

import com.proyecto_backend.eventos.domain.ReservaVisitaCreadaEvent;
import com.proyecto_backend.eventos.service.EmailService;
import com.proyecto_backend.eventos.service.QRService;
import com.proyecto_backend.reservaVisita.Domain.ReservaVisita;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservaVisitaEventListener {

    private final EmailService emailService;
    private final QRService qrService;

    @EventListener
    public void handleReservaVisitaCreada(ReservaVisitaCreadaEvent event) {
        ReservaVisita reserva = event.getReservaVisita();

        log.info("📅 Procesando evento de reserva creada ID: {} - Cliente: {} - Agente: {}",
                reserva.getId(),
                reserva.getCliente().getEmail(),
                reserva.getAgente().getEmail());

        try {
            // Generar QR con información de la reserva
            String qrData = String.format("RESERVA:%d|CLIENTE:%s|AGENTE:%s|FECHA:%s",
                    reserva.getId(),
                    reserva.getCliente().getEmail(),
                    reserva.getAgente().getEmail(),
                    reserva.getFechaHora().toString());

            byte[] qr = qrService.generarQR(qrData);
            log.debug("🔲 QR generado para reserva ID: {}", reserva.getId());

            // Enviar email al cliente con QR
            emailService.enviarCorreoReservaConQR(reserva, qr);
            log.info("✅ Email con QR enviado al cliente: {}", reserva.getCliente().getEmail());

            // Notificar al agente
            emailService.enviarCorreoNotificacionAgente(reserva);
            log.info("✅ Notificación enviada al agente: {}", reserva.getAgente().getEmail());

        } catch (Exception e) {
            log.error("❌ Error procesando evento de reserva ID: {} - Error: {}",
                    reserva.getId(), e.getMessage(), e);
        }
    }
}