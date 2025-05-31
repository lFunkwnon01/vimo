package com.proyecto_backend.usuario.Controller;


import com.proyecto_backend.usuario.Domain.Roles;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Domain.UsuarioService;
import com.proyecto_backend.usuario.Dto.UsuarioRequest;
import com.proyecto_backend.usuario.Dto.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@Valid @RequestBody UsuarioRequest dto) {
        return usuarioService.crear(dto);
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return usuarioService.obtener(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id,
                                      @Valid @RequestBody UsuarioRequest dto) {
        return usuarioService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }

    @GetMapping("/por-email")
    public UsuarioResponse buscarPorEmail(@RequestParam String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return usuarioService.mapToResponse(usuario);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/por-rol")
    public List<UsuarioResponse> listarPorRol(@RequestParam Roles rol) {
        return usuarioService.listarPorRol(rol);
    }

    @GetMapping("/buscar-nombre")
    public List<UsuarioResponse> buscarPorNombre(@RequestParam String nombre) {
        return usuarioService.buscarPorNombre(nombre);
    }

    @GetMapping("/activos")
    public List<UsuarioResponse> listarActivos() {
        return usuarioService.listarActivos();
    }
}