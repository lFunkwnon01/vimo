package com.proyecto_backend.reservaVisita.Controller;


import com.proyecto_backend.reservaVisita.Domain.ReservaVisitaService;
import com.proyecto_backend.reservaVisita.Dto.ReservaVisitaRequest;
import com.proyecto_backend.reservaVisita.Dto.ReservaVisitaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas-visita")
@RequiredArgsConstructor
public class ReservaVisitaController {

    private final ReservaVisitaService reservaVisitaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaVisitaResponse crear(@Valid @RequestBody ReservaVisitaRequest dto) {
        return reservaVisitaService.crear(dto);
    }

    @GetMapping("/{id}")
    public ReservaVisitaResponse obtener(@PathVariable Long id) {
        return reservaVisitaService.obtener(id);
    }

    @GetMapping
    public List<ReservaVisitaResponse> listar() {
        return reservaVisitaService.listar();
    }

    @PutMapping("/{id}")
    public ReservaVisitaResponse actualizar(@PathVariable Long id,
                                            @Valid @RequestBody ReservaVisitaRequest dto) {
        return reservaVisitaService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        reservaVisitaService.eliminar(id);
    }

    // Métodos personalizados

    @GetMapping("/por-propiedad/{propiedadId}")
    public List<ReservaVisitaResponse> listarPorPropiedad(@PathVariable Long propiedadId) {
        return reservaVisitaService.listarPorPropiedad(propiedadId);
    }

    @GetMapping("/por-cliente/{clienteId}")
    public List<ReservaVisitaResponse> listarPorCliente(@PathVariable Long clienteId) {
        return reservaVisitaService.listarPorCliente(clienteId);
    }

    @GetMapping("/por-agente/{agenteId}")
    public List<ReservaVisitaResponse> listarPorAgente(@PathVariable Long agenteId) {
        return reservaVisitaService.listarPorAgente(agenteId);
    }

    @GetMapping("/por-propiedad-fecha")
    public List<ReservaVisitaResponse> listarPorPropiedadYFecha(
            @RequestParam Long propiedadId,
            @RequestParam String fecha // formato: yyyy-MM-dd
    ) {
        LocalDate localDate = LocalDate.parse(fecha);
        return reservaVisitaService.listarPorPropiedadYFecha(propiedadId, localDate);
    }
    @PutMapping("/{id}/confirmar-asistencia")
    public ReservaVisitaResponse confirmarAsistencia(@PathVariable Long id) {
        return reservaVisitaService.confirmarAsistencia(id);
    }
}