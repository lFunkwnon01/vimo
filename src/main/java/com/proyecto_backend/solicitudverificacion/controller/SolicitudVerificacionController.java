package com.proyecto_backend.solicitudverificacion.controller;

import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.solicitudverificacion.domain.EstadoSolicitud;
import com.proyecto_backend.solicitudverificacion.domain.SolicitudVerificacion;
import com.proyecto_backend.solicitudverificacion.dto.SolicitudVerificacionRequest;
import com.proyecto_backend.solicitudverificacion.dto.SolicitudVerificacionResponse;
import com.proyecto_backend.solicitudverificacion.repository.SolicitudVerificacionRepository;
import com.proyecto_backend.solicitudverificacion.domain.VerificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/solicitudes-verificacion")
@RequiredArgsConstructor
public class SolicitudVerificacionController {

    private final VerificacionService verificacionService;
    private final SolicitudVerificacionRepository solicitudRepository;

    @PostMapping
    public ResponseEntity<SolicitudVerificacionResponse> crearSolicitud(@Valid @RequestBody SolicitudVerificacionRequest request) {
        SolicitudVerificacion solicitud = verificacionService.crearSolicitud(request);
        return ResponseEntity.ok(mapToResponse(solicitud));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudVerificacionResponse> obtenerSolicitud(@PathVariable Long id) {
        SolicitudVerificacion solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SolicitudVerificacion", id));
        return ResponseEntity.ok(mapToResponse(solicitud));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SolicitudVerificacionResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<SolicitudVerificacion> solicitudes = solicitudRepository.findByUsuarioId(usuarioId);
        List<SolicitudVerificacionResponse> response = solicitudes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<SolicitudVerificacionResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoSolicitud estado,
            @RequestParam Long adminId,
            @RequestParam(required = false) String comentarios) {

        SolicitudVerificacion solicitudActualizada = verificacionService.actualizarEstado(id, estado, adminId, comentarios);
        return ResponseEntity.ok(mapToResponse(solicitudActualizada));
    }

    private SolicitudVerificacionResponse mapToResponse(SolicitudVerificacion solicitud) {
        return SolicitudVerificacionResponse.builder()
                .id(solicitud.getId())
                .usuarioId(solicitud.getUsuario().getId())
                .estado(solicitud.getEstado())
                .documentoAdjunto(solicitud.getDocumentoAdjunto())
                .comentarios(solicitud.getComentarios())
                .adminAprobadorId(solicitud.getAdminAprobador() != null ? solicitud.getAdminAprobador().getId() : null)
                .fechaAprobacion(solicitud.getFechaAprobacion())
                .fechaSolicitud(solicitud.getFechaSolicitud())
                .build();
    }
}