package com.proyecto_backend.eventos.service;

import com.proyecto_backend.propiedad.Domain.Propiedad;
import com.proyecto_backend.transaccion.Domain.Transaccion;
import com.proyecto_backend.ubicaciones.domain.UbicacionGeografica;
import com.proyecto_backend.reservaVisita.Domain.ReservaVisita;
import com.proyecto_backend.usuario.Domain.Usuario;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateService templateService;

    /**
     * Envía notificación de nueva propiedad a usuarios interesados
     */
    public void enviarNotificacionNuevaPropiedad(Usuario usuario, Propiedad propiedad, boolean esActualizacion) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(usuario.getEmail());
            helper.setSubject(esActualizacion
                    ? "¡Se actualizó una propiedad en tu zona de interés!"
                    : "¡Nueva propiedad publicada en tu zona de interés!");

            // Preparar variables para el template
            Map<String, String> variables = new HashMap<>();
            variables.put("nombre_usuario", usuario.getNombre());
            variables.put("titulo_email", esActualizacion ? "Propiedad Actualizada" : "Nueva Propiedad Disponible");
            variables.put("titulo_propiedad", propiedad.getTitulo());
            variables.put("ubicacion", extraerUbicacionTexto(propiedad));
            variables.put("tipo", propiedad.getTipo().toString());
            variables.put("direccion", propiedad.getDireccion());
            variables.put("precio", String.format("S/ %.2f", propiedad.getPrecio()));
            variables.put("area", propiedad.getMetrosCuadrados().toString() + " m²");
            variables.put("estado", propiedad.getEstado().toString());
            variables.put("descripcion", propiedad.getDescripcion());
            variables.put("link_propiedad", "https://tu-sitio.com/propiedades/" + propiedad.getId());
            variables.put("link_unsubscribe", "https://tu-sitio.com/unsubscribe/" + usuario.getId());

            // Procesar template
            String htmlContent = templateService.procesarTemplate("nueva-propiedad", variables);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("✅ Notificación de nueva propiedad enviada a: {}", usuario.getEmail());
        } catch (MessagingException e) {
            log.error("❌ Error enviando notificación de nueva propiedad a: {}", usuario.getEmail(), e);
            throw new RuntimeException("Error enviando correo", e);
        }
    }

    /**
     * Envía correo de confirmación de reserva con QR al cliente
     */
    public void enviarCorreoReservaConQR(ReservaVisita reserva, byte[] qr) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(reserva.getCliente().getEmail());
            helper.setSubject("✅ Confirmación de Reserva de Visita - " + reserva.getPropiedad().getTitulo());

            // Variables para template de reserva
            Map<String, String> variables = new HashMap<>();
            variables.put("nombre_cliente", reserva.getCliente().getNombre());
            variables.put("apellido_cliente", reserva.getCliente().getApellido());
            variables.put("titulo_propiedad", reserva.getPropiedad().getTitulo());
            variables.put("fecha_hora", reserva.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            variables.put("direccion", reserva.getPropiedad().getDireccion());
            variables.put("nombre_agente", reserva.getAgente().getNombre() + " " + reserva.getAgente().getApellido());
            variables.put("telefono_agente", reserva.getAgente().getTelefono() != null ? reserva.getAgente().getTelefono() : "No disponible");
            variables.put("email_agente", reserva.getAgente().getEmail());
            variables.put("comentarios", reserva.getComentarios() != null ? reserva.getComentarios() : "Sin comentarios adicionales");
            variables.put("id_reserva", reserva.getId().toString());

            String htmlContent = templateService.procesarTemplate("reserva-confirmada", variables);
            helper.setText(htmlContent, true);

            // Adjuntar QR
            helper.addAttachment("codigo-qr-reserva.png", new ByteArrayResource(qr));

            mailSender.send(message);
            log.info("✅ Correo de reserva con QR enviado a: {}", reserva.getCliente().getEmail());
        } catch (MessagingException e) {
            log.error("❌ Error enviando correo de reserva a: {}", reserva.getCliente().getEmail(), e);
            throw new RuntimeException("Error enviando correo de reserva", e);
        }
    }

    /**
     * Envía notificación al agente sobre nueva reserva
     */
    public void enviarCorreoNotificacionAgente(ReservaVisita reserva) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(reserva.getAgente().getEmail());
            helper.setSubject("🏠 Nueva Reserva de Visita - " + reserva.getPropiedad().getTitulo());

            // Variables para template de notificación al agente
            Map<String, String> variables = new HashMap<>();
            variables.put("nombre_agente", reserva.getAgente().getNombre());
            variables.put("apellido_agente", reserva.getAgente().getApellido());
            variables.put("titulo_propiedad", reserva.getPropiedad().getTitulo());
            variables.put("fecha_hora", reserva.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            variables.put("direccion", reserva.getPropiedad().getDireccion());
            variables.put("nombre_cliente", reserva.getCliente().getNombre() + " " + reserva.getCliente().getApellido());
            variables.put("telefono_cliente", reserva.getCliente().getTelefono() != null ? reserva.getCliente().getTelefono() : "No disponible");
            variables.put("email_cliente", reserva.getCliente().getEmail());
            variables.put("comentarios", reserva.getComentarios() != null ? reserva.getComentarios() : "Sin comentarios del cliente");
            variables.put("id_reserva", reserva.getId().toString());
            variables.put("link_confirmar", "https://tu-sitio.com/agente/reservas/" + reserva.getId() + "/confirmar");

            String htmlContent = templateService.procesarTemplate("notificacion-agente", variables);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("✅ Notificación de reserva enviada al agente: {}", reserva.getAgente().getEmail());
        } catch (MessagingException e) {
            log.error("❌ Error enviando notificación al agente: {}", reserva.getAgente().getEmail(), e);
            throw new RuntimeException("Error enviando notificación al agente", e);
        }
    }

    /**
     * Envía correo de agradecimiento después de la visita
     */
    public void enviarCorreoAgradecimiento(ReservaVisita reserva) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(reserva.getCliente().getEmail());
            helper.setSubject("💙 Gracias por tu visita - " + reserva.getPropiedad().getTitulo());

            // Variables para template de agradecimiento
            Map<String, String> variables = new HashMap<>();
            variables.put("nombre_cliente", reserva.getCliente().getNombre());
            variables.put("apellido_cliente", reserva.getCliente().getApellido());
            variables.put("titulo_propiedad", reserva.getPropiedad().getTitulo());
            variables.put("fecha_visita", reserva.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            variables.put("nombre_agente", reserva.getAgente().getNombre() + " " + reserva.getAgente().getApellido());
            variables.put("telefono_agente", reserva.getAgente().getTelefono() != null ? reserva.getAgente().getTelefono() : "No disponible");
            variables.put("email_agente", reserva.getAgente().getEmail());
            variables.put("link_propiedades", "https://tu-sitio.com/propiedades");
            variables.put("link_contacto", "https://tu-sitio.com/contacto");
            variables.put("link_encuesta", "https://tu-sitio.com/encuesta/" + reserva.getId());

            String htmlContent = templateService.procesarTemplate("agradecimiento-visita", variables);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("✅ Correo de agradecimiento enviado a: {}", reserva.getCliente().getEmail());
        } catch (MessagingException e) {
            log.error("❌ Error enviando correo de agradecimiento a: {}", reserva.getCliente().getEmail(), e);
            throw new RuntimeException("Error enviando correo de agradecimiento", e);
        }
    }

    /**
     * Envía encuesta de satisfacción (método opcional para futuro)
     */
    public void enviarEncuestaSatisfaccion(ReservaVisita reserva) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(reserva.getCliente().getEmail());
            helper.setSubject("📝 Tu opinión es importante - Encuesta de Satisfacción");

            Map<String, String> variables = new HashMap<>();
            variables.put("nombre_cliente", reserva.getCliente().getNombre());
            variables.put("titulo_propiedad", reserva.getPropiedad().getTitulo());
            variables.put("link_encuesta", "https://tu-sitio.com/encuesta/" + reserva.getId());

            String htmlContent = templateService.procesarTemplate("encuesta-satisfaccion", variables);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("✅ Encuesta de satisfacción enviada a: {}", reserva.getCliente().getEmail());
        } catch (MessagingException e) {
            log.error("❌ Error enviando encuesta de satisfacción a: {}", reserva.getCliente().getEmail(), e);
            throw new RuntimeException("Error enviando encuesta", e);
        }
    }
    public void enviarCorreoConfirmacionTransaccion(Transaccion transaccion) {
        try {
            // Correo al cliente
            MimeMessage messageCliente = mailSender.createMimeMessage();
            MimeMessageHelper helperCliente = new MimeMessageHelper(messageCliente, true, "UTF-8");
            helperCliente.setTo(transaccion.getCliente().getEmail());
            helperCliente.setSubject("Confirmación de Transacción Completada");

            Map<String, String> variablesCliente = new HashMap<>();
            variablesCliente.put("nombre_cliente", transaccion.getCliente().getNombre());
            variablesCliente.put("titulo_propiedad", transaccion.getPropiedad().getTitulo());
            variablesCliente.put("monto", String.format("S/ %.2f", transaccion.getMonto()));
            variablesCliente.put("fecha", transaccion.getFecha().toLocalDate().toString());

            String htmlCliente = templateService.procesarTemplate("transaccion-completada-cliente", variablesCliente);
            helperCliente.setText(htmlCliente, true);
            mailSender.send(messageCliente);
            log.info("Correo de confirmación enviado al cliente: {}", transaccion.getCliente().getEmail());

            // Correo al propietario
            MimeMessage messagePropietario = mailSender.createMimeMessage();
            MimeMessageHelper helperPropietario = new MimeMessageHelper(messagePropietario, true, "UTF-8");
            helperPropietario.setTo(transaccion.getPropiedad().getPropietario().getEmail());
            helperPropietario.setSubject("Confirmación de Transacción Completada - Propiedad Vendida/Alquilada");

            Map<String, String> variablesPropietario = new HashMap<>();
            variablesPropietario.put("nombre_propietario", transaccion.getPropiedad().getPropietario().getNombre());
            variablesPropietario.put("titulo_propiedad", transaccion.getPropiedad().getTitulo());
            variablesPropietario.put("monto", String.format("S/ %.2f", transaccion.getMonto()));
            variablesPropietario.put("fecha", transaccion.getFecha().toLocalDate().toString());

            String htmlPropietario = templateService.procesarTemplate("transaccion-completada-propietario", variablesPropietario);
            helperPropietario.setText(htmlPropietario, true);
            mailSender.send(messagePropietario);
            log.info("Correo de confirmación enviado al propietario: {}", transaccion.getPropiedad().getPropietario().getEmail());

        } catch (MessagingException e) {
            log.error("Error enviando correos de confirmación de transacción", e);
            throw new RuntimeException("Error enviando correos de confirmación de transacción", e);
        }
    }

    public void enviarCorreoNotificacionVerificacionAdmin(Usuario usuario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo("admin@tuinmobiliaria.com"); // Cambiar por email real de admin
            helper.setSubject("Nueva Solicitud de Verificación de Usuario");

            Map<String, String> variables = new HashMap<>();
            variables.put("nombre_usuario", usuario.getNombre() + " " + usuario.getApellido());
            variables.put("email_usuario", usuario.getEmail());
            variables.put("rol_solicitado", usuario.getRol().toString());
            variables.put("documento_verificacion", usuario.getDocumentoVerificacion());

            String html = templateService.procesarTemplate("verificacion-pendiente-admin", variables);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Correo de notificación de verificación enviado al admin");
        } catch (MessagingException e) {
            log.error("Error enviando correo de notificación de verificación al admin", e);
            throw new RuntimeException("Error enviando correo de notificación de verificación al admin", e);
        }
    }

    public void enviarCorreoResultadoVerificacion(Usuario usuario, boolean aprobado) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(usuario.getEmail());
            helper.setSubject(aprobado ? "Verificación Aprobada" : "Verificación Rechazada");

            Map<String, String> variables = new HashMap<>();
            variables.put("nombre_usuario", usuario.getNombre());
            variables.put("rol", usuario.getRol().toString());

            String templateName = aprobado ? "verificacion-aprobada" : "verificacion-rechazada";
            String html = templateService.procesarTemplate(templateName, variables);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Correo de resultado de verificación enviado a: {}", usuario.getEmail());
        } catch (MessagingException e) {
            log.error("Error enviando correo de resultado de verificación", e);
            throw new RuntimeException("Error enviando correo de resultado de verificación", e);
        }
    }

    /**
     * Extrae texto legible de la ubicación de una propiedad
     */
    private String extraerUbicacionTexto(Propiedad propiedad) {
        UbicacionGeografica ubicacion = propiedad.getUbicacion();

        if (ubicacion == null) {
            return propiedad.getDireccion();
        }

        StringBuilder ubicacionTexto = new StringBuilder();

        if (ubicacion.getDistrito() != null) {
            ubicacionTexto.append(ubicacion.getDistrito());
        }

        if (ubicacion.getProvincia() != null) {
            if (ubicacionTexto.length() > 0) ubicacionTexto.append(", ");
            ubicacionTexto.append(ubicacion.getProvincia());
        }

        if (ubicacion.getRegion() != null) {
            if (ubicacionTexto.length() > 0) ubicacionTexto.append(", ");
            ubicacionTexto.append(ubicacion.getRegion());
        }

        return ubicacionTexto.length() > 0 ? ubicacionTexto.toString() : propiedad.getDireccion();
    }
}