package com.proyecto_backend.transaccion.Controller;


import com.proyecto_backend.transaccion.Domain.TransaccionService;
import com.proyecto_backend.transaccion.Dto.TransaccionRequest;
import com.proyecto_backend.transaccion.Dto.TransaccionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransaccionResponse crear(@Valid @RequestBody TransaccionRequest dto) {
        return transaccionService.crear(dto);
    }

    @GetMapping("/{id}")
    public TransaccionResponse obtener(@PathVariable Long id) {
        return transaccionService.obtener(id);
    }

    @GetMapping
    public List<TransaccionResponse> listar() {
        return transaccionService.listar();
    }

    @PutMapping("/{id}")
    public TransaccionResponse actualizar(@PathVariable Long id,
                                          @Valid @RequestBody TransaccionRequest dto) {
        return transaccionService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        transaccionService.eliminar(id);
    }

    // Métodos personalizados

    @GetMapping("/por-cliente/{clienteId}")
    public List<TransaccionResponse> listarPorCliente(@PathVariable Long clienteId) {
        return transaccionService.listarPorCliente(clienteId);
    }

    @GetMapping("/por-agente/{agenteId}")
    public List<TransaccionResponse> listarPorAgente(@PathVariable Long agenteId) {
        return transaccionService.listarPorAgente(agenteId);
    }

    @GetMapping("/por-propiedad/{propiedadId}")
    public List<TransaccionResponse> listarPorPropiedad(@PathVariable Long propiedadId) {
        return transaccionService.listarPorPropiedad(propiedadId);
    }

    @GetMapping("/por-agente-mes")
    public List<TransaccionResponse> listarPorAgenteYMes(
            @RequestParam Long agenteId,
            @RequestParam int anio,
            @RequestParam int mes
    ) {
        return transaccionService.listarPorAgenteYMes(agenteId, anio, mes);
    }
}