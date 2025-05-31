package com.proyecto_backend.preferenciaNotificacion.controller;

import com.proyecto_backend.preferenciaNotificacion.domain.PreferenciaNotificacionService;
import com.proyecto_backend.preferenciaNotificacion.domain.TipoBusqueda;
import com.proyecto_backend.preferenciaNotificacion.dto.PreferenciaNotificacionRequest;
import com.proyecto_backend.preferenciaNotificacion.dto.PreferenciaNotificacionResponse;
import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preferencias-notificacion")
@RequiredArgsConstructor
public class PreferenciaNotificacionController {

    private final PreferenciaNotificacionService preferenciaNotificacionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PreferenciaNotificacionResponse crear(@Valid @RequestBody PreferenciaNotificacionRequest dto) {
        return preferenciaNotificacionService.crear(dto);
    }

    @GetMapping("/{id}")
    public PreferenciaNotificacionResponse obtener(@PathVariable Long id) {
        return preferenciaNotificacionService.obtener(id);
    }

    @GetMapping
    public List<PreferenciaNotificacionResponse> listar() {
        return preferenciaNotificacionService.listar();
    }

    @GetMapping("/por-usuario/{usuarioId}")
    public List<PreferenciaNotificacionResponse> listarPorUsuario(@PathVariable Long usuarioId) {
        return preferenciaNotificacionService.listarPorUsuario(usuarioId);
    }

    // ========== ENDPOINTS NUEVOS GEOGRÁFICOS ==========

    @GetMapping("/por-region-interes")
    public List<PreferenciaNotificacionResponse> buscarPorRegionInteres(
            @RequestParam String region,
            @RequestParam TipoTransaccion tipo) {
        return preferenciaNotificacionService.buscarPorRegionInteres(region, tipo);
    }

    @GetMapping("/por-distrito-interes")
    public List<PreferenciaNotificacionResponse> buscarPorDistritoInteres(
            @RequestParam String distrito,
            @RequestParam TipoTransaccion tipo) {
        return preferenciaNotificacionService.buscarPorDistritoInteres(distrito, tipo);
    }

    @GetMapping("/por-tipo-busqueda")
    public List<PreferenciaNotificacionResponse> buscarPorTipoBusqueda(
            @RequestParam TipoBusqueda tipoBusqueda,
            @RequestParam TipoTransaccion tipo) {
        return preferenciaNotificacionService.buscarPorTipoBusqueda(tipoBusqueda, tipo);
    }

    @PutMapping("/{id}")
    public PreferenciaNotificacionResponse actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody PreferenciaNotificacionRequest dto) {
        return preferenciaNotificacionService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        preferenciaNotificacionService.eliminar(id);
    }
}