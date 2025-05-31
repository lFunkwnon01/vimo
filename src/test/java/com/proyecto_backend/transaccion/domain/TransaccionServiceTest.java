package com.proyecto_backend.transaccion.domain;

import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.propiedad.Domain.Propiedad;
import com.proyecto_backend.propiedad.Repository.PropiedadRepository;
import com.proyecto_backend.transaccion.Domain.Transaccion;
import com.proyecto_backend.transaccion.Domain.TransaccionService;
import com.proyecto_backend.transaccion.Dto.TransaccionRequest;
import com.proyecto_backend.transaccion.Dto.TransaccionResponse;
import com.proyecto_backend.transaccion.Repository.TransaccionRepository;
import com.proyecto_backend.usuario.Domain.Roles;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private PropiedadRepository propiedadRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TransaccionService transaccionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crear_deberiaCrearTransaccionCorrectamente() {
        Long propiedadId = 1L;
        Long clienteId = 2L;
        Long agenteId = 3L;

        TransaccionRequest request = TransaccionRequest.builder()
                .propiedadId(propiedadId)
                .clienteId(clienteId)
                .agenteId(agenteId)
                .tipo(null) // Puedes asignar un valor válido si quieres
                .monto(1000.0)
                .comisionAgente(100.0)
                .detalles("Detalles de prueba")
                .build();

        Propiedad propiedad = Propiedad.builder().id(propiedadId).build();
        Usuario cliente = Usuario.builder().id(clienteId).rol(Roles.CLIENTE).build();
        Usuario agente = Usuario.builder().id(agenteId).rol(Roles.AGENTE).build();

        when(propiedadRepository.findById(propiedadId)).thenReturn(Optional.of(propiedad));
        when(usuarioRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(agenteId)).thenReturn(Optional.of(agente));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(invocation -> {
            Transaccion t = invocation.getArgument(0);
            t.setId(100L);
            return t;
        });

        TransaccionResponse response = transaccionService.crear(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(propiedadId, response.getPropiedadId());
        assertEquals(clienteId, response.getClienteId());
        assertEquals(agenteId, response.getAgenteId());
        assertEquals(1000.0, response.getMonto());
        assertEquals(100.0, response.getComisionAgente());
        assertEquals("Detalles de prueba", response.getDetalles());

        // Verificamos que el evento fue publicado exactamente una vez
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    void crear_deberiaLanzarExcepcionSiPropiedadNoExiste() {
        TransaccionRequest request = TransaccionRequest.builder()
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .tipo(null)
                .monto(1000.0)
                .comisionAgente(100.0)
                .build();

        when(propiedadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transaccionService.crear(request));
    }

    @Test
    void crear_deberiaLanzarExcepcionSiClienteNoExiste() {
        TransaccionRequest request = TransaccionRequest.builder()
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .tipo(null)
                .monto(1000.0)
                .comisionAgente(100.0)
                .build();

        Propiedad propiedad = Propiedad.builder().id(1L).build();

        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedad));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transaccionService.crear(request));
    }

    @Test
    void crear_deberiaLanzarExcepcionSiAgenteNoExiste() {
        TransaccionRequest request = TransaccionRequest.builder()
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .tipo(null)
                .monto(1000.0)
                .comisionAgente(100.0)
                .build();

        Propiedad propiedad = Propiedad.builder().id(1L).build();
        Usuario cliente = Usuario.builder().id(2L).rol(Roles.CLIENTE).build();

        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedad));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transaccionService.crear(request));
    }

    @Test
    void crear_deberiaLanzarExcepcionSiClienteNoEsCliente() {
        TransaccionRequest request = TransaccionRequest.builder()
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .tipo(null)
                .monto(1000.0)
                .comisionAgente(100.0)
                .build();

        Propiedad propiedad = Propiedad.builder().id(1L).build();
        Usuario cliente = Usuario.builder().id(2L).rol(Roles.AGENTE).build();
        Usuario agente = Usuario.builder().id(3L).rol(Roles.AGENTE).build();

        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedad));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(agente));

        assertThrows(IllegalArgumentException.class, () -> transaccionService.crear(request));
    }

    @Test
    void crear_deberiaLanzarExcepcionSiAgenteNoEsAgente() {
        TransaccionRequest request = TransaccionRequest.builder()
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .tipo(null)
                .monto(1000.0)
                .comisionAgente(100.0)
                .build();

        Propiedad propiedad = Propiedad.builder().id(1L).build();
        Usuario cliente = Usuario.builder().id(2L).rol(Roles.CLIENTE).build();
        Usuario agente = Usuario.builder().id(3L).rol(Roles.CLIENTE).build();

        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedad));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(agente));

        assertThrows(IllegalArgumentException.class, () -> transaccionService.crear(request));
    }

    // Puedes agregar más tests para actualizar, eliminar, listar, etc.
}