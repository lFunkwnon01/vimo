package com.proyecto_backend.publicacion.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto_backend.config.JwtAuthenticationFilter;
import com.proyecto_backend.publicacion.Controller.PublicacionController;
import com.proyecto_backend.publicacion.Domain.EstadoPublicacion;
import com.proyecto_backend.publicacion.Dto.PublicacionRequest;
import com.proyecto_backend.publicacion.Dto.PublicacionResponse;
import com.proyecto_backend.publicacion.Domain.PublicacionService;
import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
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

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@WebMvcTest(controllers = PublicacionController.class)
@AutoConfigureMockMvc(addFilters = false)  // Deshabilita filtros de seguridad para tests
@Import(PublicacionControllerTest.TestConfig.class)
class PublicacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PublicacionService publicacionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crear_deberiaRetornarPublicacionCreada() throws Exception {
        PublicacionRequest request = PublicacionRequest.builder()
                .propiedadId(1L)
                .creadorId(2L)
                .estado(EstadoPublicacion.ACTIVA)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(10))
                .tipoTransaccion(TipoTransaccion.VENTA)
                .build();

        PublicacionResponse response = PublicacionResponse.builder()
                .id(100L)
                .propiedadId(1L)
                .creadorId(2L)
                .estado(EstadoPublicacion.ACTIVA)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(10))
                .tipoTransaccion(TipoTransaccion.VENTA)
                .agentesIds(Collections.emptyList())
                .build();

        Mockito.when(publicacionService.crear(any(PublicacionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/publicaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.propiedadId").value(1L))
                .andExpect(jsonPath("$.creadorId").value(2L));
    }

    @Configuration
    static class TestConfig {

        @Bean
        public PublicacionService publicacionService() {
            return Mockito.mock(PublicacionService.class);
        }
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return Mockito.mock(JwtAuthenticationFilter.class);
        }
    }
}