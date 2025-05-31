package com.proyecto_backend.usuario.Repository;





import com.proyecto_backend.usuario.Domain.Roles;
import com.proyecto_backend.usuario.Domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByRol(Roles rol);
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    List<Usuario> findByActivoTrue();
}