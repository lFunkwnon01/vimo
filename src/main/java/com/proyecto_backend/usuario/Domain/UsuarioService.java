package com.proyecto_backend.usuario.Domain;





import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.usuario.Domain.Roles;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Dto.UsuarioRequest;
import com.proyecto_backend.usuario.Dto.UsuarioResponse;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService  implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        return new User(
                usuario.getEmail(),
                usuario.getPasswordHash(),
                usuario.getActivo(),
                true,
                true,
                true,
                mapRolesToAuthorities(usuario.getRol())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Roles rol) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }
    public UsuarioResponse crear(UsuarioRequest dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("El email ya está registrado");

        Usuario usuario = mapToEntity(dto);
        usuario.setPasswordHash(encoder.encode(dto.getPassword()));

        // Si se registra como agente o propietario, aún no está verificado
        if (dto.getRol() == Roles.AGENTE || dto.getRol() == Roles.PROPIETARIO) {
            usuario.setVerificado(false);
        }

        return mapToResponse(usuarioRepository.save(usuario));
    }

    public UsuarioResponse obtener(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        return mapToResponse(u);
    }

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UsuarioResponse actualizar(Long id, UsuarioRequest dto) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        if (!u.getEmail().equals(dto.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail()))
                throw new IllegalArgumentException("El email ya está registrado");
            u.setEmail(dto.getEmail());
        }

        u.setNombre(dto.getNombre());
        u.setApellido(dto.getApellido());
        u.setTelefono(dto.getTelefono());
        u.setRol(dto.getRol());

        // Si cambia a agente o propietario, debe volver a verificarse
        if ((dto.getRol() == Roles.AGENTE || dto.getRol() == Roles.PROPIETARIO) && !u.getVerificado()) {
            u.setVerificado(false);
        }

        if (dto.getDocumentoVerificacion() != null) {
            u.setDocumentoVerificacion(dto.getDocumentoVerificacion());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            u.setPasswordHash(encoder.encode(dto.getPassword()));

        return mapToResponse(usuarioRepository.save(u));
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id))
            throw new ResourceNotFoundException("Usuario", id);
        usuarioRepository.deleteById(id);
    }

    private Usuario mapToEntity(UsuarioRequest d) {
        return Usuario.builder()
                .nombre(d.getNombre())
                .apellido(d.getApellido())
                .email(d.getEmail())
                .telefono(d.getTelefono())
                .rol(d.getRol())
                .documentoVerificacion(d.getDocumentoVerificacion())
                .build();
    }

    public UsuarioResponse mapToResponse(Usuario u) {
        return UsuarioResponse.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .email(u.getEmail())
                .telefono(u.getTelefono())
                .rol(u.getRol())
                .activo(u.getActivo())
                .verificado(u.getVerificado())
                .documentoVerificacion(u.getDocumentoVerificacion())
                .creadoEn(u.getCreadoEn())
                .build();
    }
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", email));
    }

    public List<UsuarioResponse> listarPorRol(Roles rol) {
        return usuarioRepository.findByRol(rol)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UsuarioResponse> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UsuarioResponse> listarActivos() {
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}