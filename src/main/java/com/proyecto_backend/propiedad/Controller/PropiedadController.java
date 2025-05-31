package com.proyecto_backend.propiedad.Controller;

import com.proyecto_backend.propiedad.Domain.EstadoPropiedad;
import com.proyecto_backend.propiedad.Domain.PropiedadService;
import com.proyecto_backend.propiedad.Domain.TipoPropiedad;
import com.proyecto_backend.propiedad.Dto.PropiedadRequest;
import com.proyecto_backend.propiedad.Dto.PropiedadResponse;
import com.proyecto_backend.ubicaciones.domain.UbicacionGeografica;
import com.proyecto_backend.ubicaciones.domain.UbicacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/propiedades")
@RequiredArgsConstructor
public class PropiedadController {

    private final PropiedadService propiedadService;
    private final UbicacionService ubicacionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropiedadResponse crear(@Valid @RequestBody PropiedadRequest dto) {
        return propiedadService.crear(dto);
    }

    @GetMapping("/{id}")
    public PropiedadResponse obtener(@PathVariable Long id) {
        return propiedadService.obtener(id);
    }

    @GetMapping
    public List<PropiedadResponse> listar() {
        return propiedadService.listar();
    }

    @PutMapping("/{id}")
    public PropiedadResponse actualizar(@PathVariable Long id,
                                        @Valid @RequestBody PropiedadRequest dto) {
        return propiedadService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        propiedadService.eliminar(id);
    }

    @GetMapping("/por-propietario/{propietarioId}")
    public List<PropiedadResponse> listarPorPropietario(@PathVariable Long propietarioId) {
        return propiedadService.listarPorPropietario(propietarioId);
    }

    @GetMapping("/por-estado")
    public List<PropiedadResponse> listarPorEstado(@RequestParam EstadoPropiedad estado) {
        return propiedadService.listarPorEstado(estado);
    }

    @GetMapping("/por-tipo")
    public List<PropiedadResponse> listarPorTipo(@RequestParam TipoPropiedad tipo) {
        return propiedadService.listarPorTipo(tipo);
    }

    @GetMapping("/por-propietario-estado")
    public List<PropiedadResponse> listarPorPropietarioYEstado(
            @RequestParam Long propietarioId,
            @RequestParam EstadoPropiedad estado) {
        return propiedadService.listarPorPropietarioYEstado(propietarioId, estado);
    }

    @GetMapping("/verificadas")
    public List<PropiedadResponse> listarVerificadas() {
        return propiedadService.listarVerificadas();
    }

    @GetMapping("/no-verificadas")
    public List<PropiedadResponse> listarNoVerificadas() {
        return propiedadService.listarNoVerificadas();
    }

    @PostMapping("/validar-direccion")
    public UbicacionGeografica validarDireccion(@RequestParam String direccion) {
        return ubicacionService.geocodificarDireccion(direccion);
    }

    @GetMapping("/autocompletar")
    public List<String> autocompletarDireccion(@RequestParam String input) {
        return ubicacionService.autocompletarDireccion(input);
    }
}