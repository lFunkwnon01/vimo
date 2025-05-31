package com.proyecto_backend.reservaVisita.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto_backend.config.JwtAuthenticationFilter;
import com.proyecto_backend.config.JwtService;
import com.proyecto_backend.reservaVisita.Controller.ReservaVisitaController;
import com.proyecto_backend.reservaVisita.Domain.EstadoReservaVisita;
import com.proyecto_backend.reservaVisita.Dto.ReservaVisitaRequest;
import com.proyecto_backend.reservaVisita.Dto.ReservaVisitaResponse;
import com.proyecto_backend.reservaVisita.Domain.ReservaVisitaService;
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
@WebMvcTest(controllers = ReservaVisitaController.class)
@AutoConfigureMockMvc(addFilters = false)  // Deshabilita filtros de seguridad para tests
@Import(ReservaVisitaControllerTest.TestConfig.class)
class ReservaVisitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservaVisitaService reservaVisitaService;

    @Autowired
    private JwtService jwtService;

    @Test
    void crear_deberiaRetornarReservaCreada() throws Exception {
        ReservaVisitaRequest request = ReservaVisitaRequest.builder()
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .estado(EstadoReservaVisita.PENDIENTE)
                .comentarios("Comentario de prueba")
                .build();

        ReservaVisitaResponse response = ReservaVisitaResponse.builder()
                .id(100L)
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .fechaHora(request.getFechaHora())
                .estado(EstadoReservaVisita.PENDIENTE)
                .comentarios("Comentario de prueba")
                .build();

        Mockito.when(reservaVisitaService.crear(any(ReservaVisitaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/reservas-visita")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.propiedadId").value(1L))
                .andExpect(jsonPath("$.clienteId").value(2L))
                .andExpect(jsonPath("$.agenteId").value(3L))
                .andExpect(jsonPath("$.comentarios").value("Comentario de prueba"));
    }

    @Test
    void obtener_deberiaRetornarReserva() throws Exception {
        ReservaVisitaResponse response = ReservaVisitaResponse.builder()
                .id(100L)
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .build();

        Mockito.when(reservaVisitaService.obtener(anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/reservas-visita/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.propiedadId").value(1L));
    }

    @Test
    void listar_deberiaRetornarListaVacia() throws Exception {
        Mockito.when(reservaVisitaService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/reservas-visita"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Configuration
    static class TestConfig {

        @Bean
        public ReservaVisitaService reservaVisitaService() {
            return Mockito.mock(ReservaVisitaService.class);
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