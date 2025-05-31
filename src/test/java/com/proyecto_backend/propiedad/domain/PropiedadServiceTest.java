package com.proyecto_backend.propiedad.domain;

import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.propiedad.Domain.EstadoPropiedad;
import com.proyecto_backend.propiedad.Domain.Propiedad;
import com.proyecto_backend.propiedad.Domain.PropiedadService;
import com.proyecto_backend.propiedad.Domain.TipoPropiedad;
import com.proyecto_backend.propiedad.Dto.PropiedadRequest;
import com.proyecto_backend.propiedad.Dto.PropiedadResponse;
import com.proyecto_backend.propiedad.Repository.PropiedadRepository;
import com.proyecto_backend.ubicaciones.domain.UbicacionGeografica;
import com.proyecto_backend.ubicaciones.domain.UbicacionService;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PropiedadServiceTest {

    @Mock
    private PropiedadRepository propiedadRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UbicacionService ubicacionService;

    @InjectMocks
    private PropiedadService propiedadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crear_deberiaCrearPropiedadCorrectamente() {
        // Datos de entrada
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

        Usuario propietario = Usuario.builder().id(1L).build();
        UbicacionGeografica ubicacion = UbicacionGeografica.builder()
                .direccionCompleta("Av. Test 123")
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(propietario));
        when(ubicacionService.geocodificarDireccion("Av. Test 123")).thenReturn(ubicacion);
        when(propiedadRepository.save(any(Propiedad.class))).thenAnswer(invocation -> {
            Propiedad p = invocation.getArgument(0);
            p.setId(10L); // Simular ID generado
            return p;
        });

        PropiedadResponse response = propiedadService.crear(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Casa Test", response.getTitulo());
        assertFalse(response.getVerificada());
        verify(usuarioRepository).findById(1L);
        verify(ubicacionService).geocodificarDireccion("Av. Test 123");
        verify(propiedadRepository).save(any(Propiedad.class));
    }

    @Test
    void crear_deberiaLanzarExcepcionSiPropietarioNoExiste() {
        PropiedadRequest request = PropiedadRequest.builder()
                .propietarioId(99L)
                .build();

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> propiedadService.crear(request));
        verify(usuarioRepository).findById(99L);
        verifyNoMoreInteractions(propiedadRepository, ubicacionService);
    }

    // Puedes agregar más tests para obtener, actualizar, eliminar, etc.
}