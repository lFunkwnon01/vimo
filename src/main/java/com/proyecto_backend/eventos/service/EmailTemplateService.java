package com.proyecto_backend.eventos.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
public class EmailTemplateService {

    private static final String TEMPLATE_PATH = "templates/email/";
    private static final String TEMPLATE_EXTENSION = ".html";

    /**
     * Procesa un template de email reemplazando las variables
     * @param templateName Nombre del template (sin extensión)
     * @param variables Mapa de variables para reemplazar
     * @return HTML procesado con variables reemplazadas
     */
    public String procesarTemplate(String templateName, Map<String, String> variables) {
        log.info("🔄 Procesando template: {}", templateName);

        try {
            // Validar entrada
            if (!StringUtils.hasText(templateName)) {
                log.warn("⚠️ Nombre de template vacío, usando genérico");
                templateName = "generico";
            }

            if (variables == null) {
                log.warn("⚠️ Variables nulas para template: {}", templateName);
                variables = Map.of(); // Mapa vacío
            }

            // Cargar template
            String template = cargarTemplateDesdeArchivo(templateName);

            // Procesar variables
            String templateProcesado = reemplazarVariables(template, variables);

            log.info("✅ Template '{}' procesado exitosamente con {} variables",
                    templateName, variables.size());

            return templateProcesado;

        } catch (Exception e) {
            log.error("❌ Error procesando template '{}': {}", templateName, e.getMessage(), e);
            return obtenerTemplateGenericoFallback();
        }
    }

    /**
     * Carga un template HTML desde el archivo en resources/templates/email/
     */
    private String cargarTemplateDesdeArchivo(String templateName) throws IOException {
        String filename = templateName + TEMPLATE_EXTENSION;
        String fullPath = TEMPLATE_PATH + filename;

        log.debug("📂 Intentando cargar template: {}", fullPath);

        try {
            ClassPathResource resource = new ClassPathResource(fullPath);

            if (!resource.exists()) {
                log.warn("⚠️ Template no encontrado: {} - Intentando con genérico", fullPath);

                // Si no es el genérico, intentar cargar el genérico
                if (!"generico".equals(templateName)) {
                    return cargarTemplateDesdeArchivo("generico");
                } else {
                    throw new IOException("Template genérico no encontrado");
                }
            }

            // Leer contenido del archivo
            byte[] bytes = resource.getInputStream().readAllBytes();
            String content = new String(bytes, StandardCharsets.UTF_8);

            log.debug("📄 Template cargado exitosamente: {} ({} caracteres)",
                    fullPath, content.length());

            return content;

        } catch (IOException e) {
            log.error("💥 Error leyendo template {}: {}", fullPath, e.getMessage());
            throw e;
        }
    }

    /**
     * Reemplaza las variables en el template
     */
    private String reemplazarVariables(String template, Map<String, String> variables) {
        if (template == null || variables == null || variables.isEmpty()) {
            return template;
        }

        String resultado = template;
        int variablesReemplazadas = 0;

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue() : "";

            // Contar cuántas veces aparece la variable
            int ocurrenciasBefore = contarOcurrencias(resultado, placeholder);

            if (ocurrenciasBefore > 0) {
                resultado = resultado.replace(placeholder, value);
                variablesReemplazadas++;

                log.debug("🔄 Variable '{}' reemplazada {} veces: '{}'",
                        entry.getKey(), ocurrenciasBefore,
                        value.length() > 50 ? value.substring(0, 50) + "..." : value);
            }
        }

        // Verificar variables no reemplazadas
        verificarVariablesNoReemplazadas(resultado);

        log.debug("✅ {} variables reemplazadas en el template", variablesReemplazadas);
        return resultado;
    }

    /**
     * Cuenta las ocurrencias de un substring en un string
     */
    private int contarOcurrencias(String texto, String substring) {
        int count = 0;
        int index = 0;
        while ((index = texto.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }

    /**
     * Verifica si quedan variables sin reemplazar en el template
     */
    private void verificarVariablesNoReemplazadas(String template) {
        if (template.contains("{{") && template.contains("}}")) {
            // Extraer variables no reemplazadas
            String[] partes = template.split("\\{\\{");
            for (int i = 1; i < partes.length; i++) {
                int endIndex = partes[i].indexOf("}}");
                if (endIndex > 0) {
                    String variableNoReemplazada = partes[i].substring(0, endIndex);
                    log.warn("⚠️ Variable no reemplazada encontrada: {}", variableNoReemplazada);
                }
            }
        }
    }

    /**
     * Template genérico hardcoded como último recurso
     */
    private String obtenerTemplateGenericoFallback() {
        log.warn("🚨 Usando template genérico hardcoded como último recurso");

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Notificación - Tu Inmobiliaria</title>
                <style>
                    body { 
                        font-family: Arial, sans-serif; 
                        line-height: 1.6; 
                        color: #333; 
                        margin: 0; 
                        padding: 20px; 
                        background-color: #f4f4f4; 
                    }
                    .container { 
                        max-width: 600px; 
                        margin: 0 auto; 
                        background: white; 
                        border-radius: 8px; 
                        overflow: hidden; 
                        box-shadow: 0 4px 6px rgba(0,0,0,0.1); 
                    }
                    .header { 
                        background: #34495e; 
                        color: white; 
                        padding: 20px; 
                        text-align: center; 
                    }
                    .content { 
                        padding: 30px; 
                        text-align: center; 
                    }
                    .footer { 
                        background: #ecf0f1; 
                        padding: 15px; 
                        text-align: center; 
                        font-size: 12px; 
                        color: #666; 
                    }
                    .error-notice { 
                        background: #fff3cd; 
                        border: 1px solid #ffeaa7; 
                        padding: 15px; 
                        border-radius: 5px; 
                        margin: 20px 0; 
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📧 Notificación</h1>
                    </div>
                    <div class="content">
                        <h2>🏠 Tu Inmobiliaria</h2>
                        <p>Tienes una nueva notificación importante.</p>
                        
                        <div class="error-notice">
                            <p><strong>⚠️ Aviso del Sistema:</strong></p>
                            <p>No se pudo cargar el template de email solicitado. Este es un mensaje genérico de respaldo.</p>
                        </div>
                        
                        <p>Si necesitas asistencia, por favor contacta a nuestro equipo de soporte.</p>
                    </div>
                    <div class="footer">
                        <p>© 2024 Tu Inmobiliaria. Todos los derechos reservados.</p>
                        <p>Este es un email generado automáticamente.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * Valida que un template existe en el sistema
     */
    public boolean existeTemplate(String templateName) {
        if (!StringUtils.hasText(templateName)) {
            return false;
        }

        String filename = templateName + TEMPLATE_EXTENSION;
        String fullPath = TEMPLATE_PATH + filename;
        ClassPathResource resource = new ClassPathResource(fullPath);

        boolean existe = resource.exists();
        log.debug("🔍 Verificación de template '{}': {}", templateName, existe ? "EXISTE" : "NO EXISTE");

        return existe;
    }

    /**
     * Lista todos los templates disponibles (útil para debugging y administración)
     */
    public String[] getTemplatesDisponibles() {
        String[] templates = {
                "nueva-propiedad",
                "reserva-confirmada",
                "notificacion-agente",
                "agradecimiento-visita",
                "encuesta-satisfaccion",
                "generico"
        };

        log.debug("📋 Templates disponibles: {}", String.join(", ", templates));
        return templates;
    }

    /**
     * Método de utilidad para crear un mapa de variables básico
     */
    public static Map<String, String> crearVariablesBasicas(String nombreUsuario, String tituloEmail) {
        return Map.of(
                "nombre_usuario", nombreUsuario != null ? nombreUsuario : "Usuario",
                "titulo_email", tituloEmail != null ? tituloEmail : "Notificación"
        );
    }

    /**
     * Valida el contenido de un template procesado
     */
    public boolean validarTemplateProcessado(String templateProcesado) {
        if (!StringUtils.hasText(templateProcesado)) {
            log.warn("⚠️ Template procesado está vacío");
            return false;
        }

        if (!templateProcesado.contains("<html") || !templateProcesado.contains("</html>")) {
            log.warn("⚠️ Template procesado no parece ser HTML válido");
            return false;
        }

        return true;
    }
}