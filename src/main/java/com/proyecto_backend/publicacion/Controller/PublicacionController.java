package com.proyecto_backend.publicacion.Controller;

import com.proyecto_backend.publicacion.Domain.PublicacionService;
import com.proyecto_backend.publicacion.Dto.PublicacionRequest;
import com.proyecto_backend.publicacion.Dto.PublicacionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publicaciones")
@RequiredArgsConstructor
public class PublicacionController {

    private final PublicacionService publicacionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PublicacionResponse crear(@Valid @RequestBody PublicacionRequest dto) {
        return publicacionService.crear(dto);
    }

    @GetMapping("/{id}")
    public PublicacionResponse obtener(@PathVariable Long id) {
        return publicacionService.obtener(id);
    }

    @GetMapping
    public List<PublicacionResponse> listar() {
        return publicacionService.listar();
    }

    @PutMapping("/{id}")
    public PublicacionResponse actualizar(@PathVariable Long id,
                                          @Valid @RequestBody PublicacionRequest dto) {
        return publicacionService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        publicacionService.eliminar(id);
    }

    // Métodos personalizados

    @GetMapping("/por-propiedad/{propiedadId}")
    public List<PublicacionResponse> listarPorPropiedad(@PathVariable Long propiedadId) {
        return publicacionService.listarPorPropiedad(propiedadId);
    }

    @GetMapping("/por-agente/{agenteId}")
    public List<PublicacionResponse> listarPorAgente(@PathVariable Long agenteId) {
        return publicacionService.listarPorAgente(agenteId);
    }

    @PostMapping("/{publicacionId}/agentes/{agenteId}")
    public PublicacionResponse agregarAgente(@PathVariable Long publicacionId, @PathVariable Long agenteId) {
        return publicacionService.agregarAgente(publicacionId, agenteId);
    }

    @DeleteMapping("/{publicacionId}/agentes/{agenteId}")
    public PublicacionResponse removerAgente(@PathVariable Long publicacionId, @PathVariable Long agenteId) {
        return publicacionService.removerAgente(publicacionId, agenteId);
    }
}
