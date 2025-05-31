package com.proyecto_backend.usuario.domain;

import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.usuario.Domain.Roles;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Domain.UsuarioService;
import com.proyecto_backend.usuario.Dto.UsuarioRequest;
import com.proyecto_backend.usuario.Dto.UsuarioResponse;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crear_deberiaCrearUsuarioCorrectamente() {
        UsuarioRequest request = UsuarioRequest.builder()
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@example.com")
                .password("password123")
                .telefono("123456789")
                .rol(Roles.CLIENTE)
                .build();

        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);

        // Simulamos el guardado y establecemos campos importantes para evitar NPE
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            u.setActivo(true); // Cliente activo por defecto
            u.setVerificado(true); // Cliente verificado por defecto
            return u;
        });

        UsuarioResponse response = usuarioService.crear(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Juan", response.getNombre());
        assertEquals("Perez", response.getApellido());
        assertEquals("juan@example.com", response.getEmail());
        assertEquals("123456789", response.getTelefono());
        assertEquals(Roles.CLIENTE, response.getRol());
        assertTrue(response.getActivo());
        assertTrue(response.getVerificado()); // Cliente verificado por defecto

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void crear_deberiaLanzarExcepcionSiEmailExiste() {
        UsuarioRequest request = UsuarioRequest.builder()
                .email("juan@example.com")
                .build();

        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.crear(request));
    }

    @Test
    void obtener_deberiaRetornarUsuario() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@example.com")
                .activo(true)
                .verificado(true)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponse response = usuarioService.obtener(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Juan", response.getNombre());
        assertTrue(response.getActivo());
        assertTrue(response.getVerificado());
    }

    @Test
    void obtener_deberiaLanzarExcepcionSiNoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.obtener(1L));
    }

    @Test
    void loadUserByUsername_deberiaRetornarUserDetails() {
        Usuario usuario = Usuario.builder()
                .email("juan@example.com")
                .passwordHash(encoder.encode("password123"))
                .rol(Roles.CLIENTE)
                .activo(true)
                .verificado(true)
                .build();

        when(usuarioRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuario));

        var userDetails = usuarioService.loadUserByUsername("juan@example.com");

        assertNotNull(userDetails);
        assertEquals("juan@example.com", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_deberiaLanzarExcepcionSiNoExiste() {
        when(usuarioRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usuarioService.loadUserByUsername("noexiste@example.com"));
    }

    // Puedes agregar más tests para actualizar, eliminar, listar, etc.
}