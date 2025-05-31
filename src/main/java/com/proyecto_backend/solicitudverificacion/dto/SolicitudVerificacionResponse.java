package com.proyecto_backend.solicitudverificacion.dto;

import com.proyecto_backend.solicitudverificacion.domain.EstadoSolicitud;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SolicitudVerificacionResponse {

    private Long id;
    private Long usuarioId;
    private EstadoSolicitud estado;
    private String documentoAdjunto;
    private String comentarios;
    private Long adminAprobadorId;
    private LocalDateTime fechaAprobacion;
    private LocalDateTime fechaSolicitud;
}