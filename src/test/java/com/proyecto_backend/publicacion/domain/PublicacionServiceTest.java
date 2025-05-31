package com.proyecto_backend.publicacion.domain;

import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.publicacion.Domain.EstadoPublicacion;
import com.proyecto_backend.publicacion.Domain.Publicacion;
import com.proyecto_backend.publicacion.Domain.PublicacionService;
import com.proyecto_backend.publicacion.Dto.PublicacionRequest;
import com.proyecto_backend.publicacion.Dto.PublicacionResponse;
import com.proyecto_backend.publicacion.Repository.PublicacionRepository;
import com.proyecto_backend.propiedad.Domain.Propiedad;
import com.proyecto_backend.propiedad.Repository.PropiedadRepository;
import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import com.proyecto_backend.usuario.Domain.Roles;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublicacionServiceTest {

    @Mock
    private PublicacionRepository publicacionRepository;

    @Mock
    private PropiedadRepository propiedadRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PublicacionService publicacionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crear_deberiaCrearPublicacionCorrectamente() {
        Long propiedadId = 1L;
        Long creadorId = 2L;

        PublicacionRequest request = PublicacionRequest.builder()
                .propiedadId(propiedadId)
                .creadorId(creadorId)
                .estado(EstadoPublicacion.ACTIVA)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(10))
                .tipoTransaccion(TipoTransaccion.VENTA)
                .build();

        Propiedad propiedad = Propiedad.builder().id(propiedadId).build();
        Usuario creador = Usuario.builder().id(creadorId).rol(Roles.PROPIETARIO).build();

        when(propiedadRepository.findById(propiedadId)).thenReturn(Optional.of(propiedad));
        when(usuarioRepository.findById(creadorId)).thenReturn(Optional.of(creador));
        when(publicacionRepository.existsByPropiedadIdAndEstadoAndFechaFinAfter(
                eq(propiedadId), eq(EstadoPublicacion.ACTIVA), any(LocalDate.class))).thenReturn(false);
        when(publicacionRepository.save(any(Publicacion.class))).thenAnswer(invocation -> {
            Publicacion p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        PublicacionResponse response = publicacionService.crear(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(propiedadId, response.getPropiedadId());
        assertEquals(creadorId, response.getCreadorId());

        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    void crear_deberiaLanzarExcepcionSiPropiedadNoExiste() {
        Long propiedadId = 1L;
        Long creadorId = 2L;

        PublicacionRequest request = PublicacionRequest.builder()
                .propiedadId(propiedadId)
                .creadorId(creadorId)
                .estado(EstadoPublicacion.ACTIVA)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(10))
                .build();

        when(propiedadRepository.findById(propiedadId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> publicacionService.crear(request));
    }

    @Test
    void crear_deberiaLanzarExcepcionSiCreadorNoExiste() {
        Long propiedadId = 1L;
        Long creadorId = 2L;

        PublicacionRequest request = PublicacionRequest.builder()
                .propiedadId(propiedadId)
                .creadorId(creadorId)
                .estado(EstadoPublicacion.ACTIVA)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(10))
                .build();

        Propiedad propiedad = Propiedad.builder().id(propiedadId).build();

        when(propiedadRepository.findById(propiedadId)).thenReturn(Optional.of(propiedad));
        when(usuarioRepository.findById(creadorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> publicacionService.crear(request));
    }

    @Test
    void crear_deberiaLanzarExcepcionSiRolNoEsPropietarioNiAdmin() {
        Long propiedadId = 1L;
        Long creadorId = 2L;

        PublicacionRequest request = PublicacionRequest.builder()
                .propiedadId(propiedadId)
                .creadorId(creadorId)
                .estado(EstadoPublicacion.ACTIVA)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusDays(10))
                .build();

        Propiedad propiedad = Propiedad.builder().id(propiedadId).build();
        Usuario creador = Usuario.builder().id(creadorId).rol(Roles.CLIENTE).build();

        when(propiedadRepository.findById(propiedadId)).thenReturn(Optional.of(propiedad));
        when(usuarioRepository.findById(creadorId)).thenReturn(Optional.of(creador));

        assertThrows(IllegalArgumentException.class, () -> publicacionService.crear(request));
    }

    // Puedes agregar más tests para actualizar, eliminar, agregar/remover agentes, etc.
}