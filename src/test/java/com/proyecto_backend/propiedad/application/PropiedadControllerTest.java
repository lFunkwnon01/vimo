package com.proyecto_backend.propiedad.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto_backend.config.JwtAuthenticationFilter;
import com.proyecto_backend.config.JwtService;
import com.proyecto_backend.propiedad.Controller.PropiedadController;
import com.proyecto_backend.propiedad.Domain.EstadoPropiedad;
import com.proyecto_backend.propiedad.Domain.TipoPropiedad;
import com.proyecto_backend.propiedad.Dto.PropiedadRequest;
import com.proyecto_backend.propiedad.Dto.PropiedadResponse;
import com.proyecto_backend.propiedad.Domain.PropiedadService;
import com.proyecto_backend.ubicaciones.domain.UbicacionService;
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
@WebMvcTest(controllers = PropiedadController.class)  // Cambiado a PropiedadController.class
@AutoConfigureMockMvc(addFilters = false)  // Deshabilita filtros de seguridad para tests
@Import(PropiedadControllerTest.TestConfig.class)  // Importamos la configuración con los mocks
class PropiedadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crear_deberiaRetornarPropiedadCreada() throws Exception {
        PropiedadRequest request = PropiedadRequest.builder()
                .titulo("Casa Test")
                .descripcion("Descripción Test")
                .direccion("Av. Test 123")
                .tipo(TipoPropiedad.CASA)
                .metrosCuadrados(100.0)
                .precio(250000.0)
                .estado(EstadoPropiedad.DISPONIBLE)
                .propietarioId(1L)
                .imagenes("img1.jpg,img2.jpg")
                .build();

        PropiedadResponse response = PropiedadResponse.builder()
                .id(10L)
                .titulo("Casa Test")
                .descripcion("Descripción Test")
                .direccion("Av. Test 123")
                .tipo(TipoPropiedad.CASA)
                .metrosCuadrados(100.0)
                .precio(250000.0)
                .estado(EstadoPropiedad.DISPONIBLE)
                .propietarioId(1L)
                .imagenes("img1.jpg,img2.jpg")
                .verificada(false)
                .build();

        Mockito.when(propiedadService.crear(any(PropiedadRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/propiedades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.titulo").value("Casa Test"))
                .andExpect(jsonPath("$.verificada").value(false));
    }

    @Test
    void obtener_deberiaRetornarPropiedad() throws Exception {
        PropiedadResponse response = PropiedadResponse.builder()
                .id(10L)
                .titulo("Casa Test")
                .build();

        Mockito.when(propiedadService.obtener(anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/propiedades/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.titulo").value("Casa Test"));
    }

    @Test
    void listar_deberiaRetornarListaVacia() throws Exception {
        Mockito.when(propiedadService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/propiedades"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Configuration
    static class TestConfig {

        @Bean
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }

        @Bean
        public PropiedadService propiedadService() {
            return Mockito.mock(PropiedadService.class);
        }

        @Bean
        public UbicacionService ubicacionService() {
            return Mockito.mock(UbicacionService.class);
        }
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return Mockito.mock(JwtAuthenticationFilter.class);
        }
    }
}