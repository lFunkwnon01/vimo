package com.proyecto_backend.eventos.domain;

import com.proyecto_backend.eventos.service.EmailService;
import com.proyecto_backend.preferenciaNotificacion.domain.PreferenciaNotificacion;
import com.proyecto_backend.preferenciaNotificacion.domain.PreferenciaNotificacionService;
import com.proyecto_backend.propiedad.Domain.Propiedad;
import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PublicacionEventListener {

    private final PreferenciaNotificacionService preferenciaNotificacionService;
    private final EmailService emailService;

    @EventListener
    public void handlePublicacionEvent(PublicacionEvent event) {
        Propiedad propiedad = event.getPropiedad();

        // Determinar tipo de transacción basado en el precio o título
        TipoTransaccion tipoTransaccion = determinarTipoTransaccion(propiedad);

        log.info("🏠 Procesando evento de publicación para propiedad ID: {} - Tipo: {}",
                propiedad.getId(), tipoTransaccion);

        try {
            // Buscar usuarios interesados usando el servicio geográfico
            List<PreferenciaNotificacion> preferencias = preferenciaNotificacionService
                    .buscarUsuariosInteresados(propiedad, tipoTransaccion);

            log.info("👥 Encontrados {} usuarios interesados para la propiedad ID: {}",
                    preferencias.size(), propiedad.getId());

            if (preferencias.isEmpty()) {
                log.info("ℹ️ No hay usuarios interesados en esta ubicación y tipo de transacción");
                return;
            }

            // Enviar notificaciones a cada usuario interesado
            int notificacionesEnviadas = 0;
            for (PreferenciaNotificacion pref : preferencias) {
                try {
                    emailService.enviarNotificacionNuevaPropiedad(
                            pref.getUsuario(),
                            propiedad,
                            event.isEsActualizacion()
                    );
                    notificacionesEnviadas++;
                    log.debug("✅ Notificación enviada a usuario ID: {} ({})",
                            pref.getUsuario().getId(), pref.getUsuario().getEmail());
                } catch (Exception e) {
                    log.error("❌ Error enviando notificación a usuario ID: {} - Error: {}",
                            pref.getUsuario().getId(), e.getMessage());
                }
            }

            log.info("📧 Proceso completado: {}/{} notificaciones enviadas para propiedad ID: {}",
                    notificacionesEnviadas, preferencias.size(), propiedad.getId());

        } catch (Exception e) {
            log.error("💥 Error general procesando evento de publicación para propiedad ID: {} - Error: {}",
                    propiedad.getId(), e.getMessage(), e);
        }
    }

    /**
     * Determina el tipo de transacción basado en características de la propiedad
     * Estrategias múltiples para mayor precisión
     */
    private TipoTransaccion determinarTipoTransaccion(Propiedad propiedad) {
        // ESTRATEGIA 1: Análisis del título
        String titulo = propiedad.getTitulo().toLowerCase();
        if (titulo.contains("alquiler") || titulo.contains("renta") ||
                titulo.contains("arriendo") || titulo.contains("alquila")) {
            log.debug("🔍 Tipo determinado por título: ALQUILER para propiedad ID: {}", propiedad.getId());
            return TipoTransaccion.ALQUILER;
        }

        if (titulo.contains("venta") || titulo.contains("vende") ||
                titulo.contains("compra") || titulo.contains("vender")) {
            log.debug("🔍 Tipo determinado por título: VENTA para propiedad ID: {}", propiedad.getId());
            return TipoTransaccion.VENTA;
        }

        // ESTRATEGIA 2: Análisis del precio (heurística)
        Double precio = propiedad.getPrecio();
        if (precio != null) {
            // Si el precio es menor a 5000, probablemente es alquiler mensual
            if (precio < 5000) {
                log.debug("💰 Tipo determinado por precio (< 5000): ALQUILER para propiedad ID: {}", propiedad.getId());
                return TipoTransaccion.ALQUILER;
            }
            // Si el precio es mayor a 50000, probablemente es venta
            if (precio > 50000) {
                log.debug("💰 Tipo determinado por precio (> 50000): VENTA para propiedad ID: {}", propiedad.getId());
                return TipoTransaccion.VENTA;
            }
        }

        // ESTRATEGIA 3: Análisis de la descripción
        String descripcion = propiedad.getDescripcion().toLowerCase();
        if (descripcion.contains("alquiler") || descripcion.contains("mensual") ||
                descripcion.contains("renta")) {
            log.debug("📝 Tipo determinado por descripción: ALQUILER para propiedad ID: {}", propiedad.getId());
            return TipoTransaccion.ALQUILER;
        }

        // DEFAULT: VENTA (la mayoría de propiedades suelen ser para venta)
        log.debug("⚙️ Tipo determinado por defecto: VENTA para propiedad ID: {}", propiedad.getId());
        return TipoTransaccion.VENTA;
    }
}