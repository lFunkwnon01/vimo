package com.proyecto_backend.solicitudverificacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolicitudVerificacionRequest {

    @NotNull(message = "El id del usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El documento adjunto es obligatorio")
    private String documentoAdjunto;

    private String comentarios;
}