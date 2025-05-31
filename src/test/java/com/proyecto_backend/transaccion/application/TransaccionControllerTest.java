package com.proyecto_backend.transaccion.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto_backend.config.JwtAuthenticationFilter;
import com.proyecto_backend.config.JwtService;
import com.proyecto_backend.transaccion.Controller.TransaccionController;
import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import com.proyecto_backend.transaccion.Dto.TransaccionRequest;
import com.proyecto_backend.transaccion.Dto.TransaccionResponse;
import com.proyecto_backend.transaccion.Domain.TransaccionService;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@WebMvcTest(controllers = TransaccionController.class)
@AutoConfigureMockMvc(addFilters = false)  // Deshabilita filtros de seguridad para tests
@Import(TransaccionControllerTest.TestConfig.class)
class TransaccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransaccionService transaccionService;

    @Autowired
    private JwtService jwtService;

    @Test
    void crear_deberiaRetornarTransaccionCreada() throws Exception {
        TransaccionRequest request = TransaccionRequest.builder()
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .tipo(TipoTransaccion.VENTA)
                .monto(1000.0)
                .comisionAgente(100.0)
                .detalles("Detalles de prueba")
                .build();

        TransaccionResponse response = TransaccionResponse.builder()
                .id(100L)
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .tipo(TipoTransaccion.VENTA)
                .monto(1000.0)
                .comisionAgente(100.0)
                .detalles("Detalles de prueba")
                .build();

        Mockito.when(transaccionService.crear(any(TransaccionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/transacciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.propiedadId").value(1L))
                .andExpect(jsonPath("$.clienteId").value(2L))
                .andExpect(jsonPath("$.agenteId").value(3L))
                .andExpect(jsonPath("$.monto").value(1000.0))
                .andExpect(jsonPath("$.comisionAgente").value(100.0))
                .andExpect(jsonPath("$.detalles").value("Detalles de prueba"));
    }

    @Test
    void obtener_deberiaRetornarTransaccion() throws Exception {
        TransaccionResponse response = TransaccionResponse.builder()
                .id(100L)
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .build();

        Mockito.when(transaccionService.obtener(anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/transacciones/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.propiedadId").value(1L));
    }

    @Test
    void listar_deberiaRetornarListaVacia() throws Exception {
        Mockito.when(transaccionService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/transacciones"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Configuration
    static class TestConfig {

        @Bean
        public TransaccionService transaccionService() {
            return Mockito.mock(TransaccionService.class);
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