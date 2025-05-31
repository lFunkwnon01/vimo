package com.proyecto_backend.usuario.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto_backend.config.JwtAuthenticationFilter;
import com.proyecto_backend.config.JwtService;
import com.proyecto_backend.usuario.Controller.UsuarioController;
import com.proyecto_backend.usuario.Domain.Roles;
import com.proyecto_backend.usuario.Dto.UsuarioRequest;
import com.proyecto_backend.usuario.Dto.UsuarioResponse;
import com.proyecto_backend.usuario.Domain.UsuarioService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)  // Deshabilita filtros de seguridad para tests
@Import(UsuarioControllerTest.TestConfig.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @Test
    void crear_deberiaRetornarUsuarioCreado() throws Exception {
        UsuarioRequest request = UsuarioRequest.builder()
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@example.com")
                .password("password123")
                .telefono("123456789")
                .rol(Roles.CLIENTE)
                .build();

        UsuarioResponse response = UsuarioResponse.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@example.com")
                .telefono("123456789")
                .rol(Roles.CLIENTE)
                .activo(true)
                .verificado(false)
                .creadoEn(LocalDateTime.now())
                .build();

        Mockito.when(usuarioService.crear(any(UsuarioRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    @Test
    void obtener_deberiaRetornarUsuario() throws Exception {
        UsuarioResponse response = UsuarioResponse.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@example.com")
                .build();

        Mockito.when(usuarioService.obtener(anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void listar_deberiaRetornarListaVacia() throws Exception {
        Mockito.when(usuarioService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Configuration
    static class TestConfig {

        @Bean
        public UsuarioService usuarioService() {
            return Mockito.mock(UsuarioService.class);
        }

        @Bean
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return Mockito.mock(JwtAuthenticationFilter.class);
        }
    }
}