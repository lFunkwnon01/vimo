package com.proyecto_backend.reservaVisita.domain;

import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.propiedad.Domain.Propiedad;
import com.proyecto_backend.propiedad.Repository.PropiedadRepository;
import com.proyecto_backend.reservaVisita.Domain.ReservaVisita;
import com.proyecto_backend.reservaVisita.Domain.ReservaVisitaService;
import com.proyecto_backend.reservaVisita.Dto.ReservaVisitaRequest;
import com.proyecto_backend.reservaVisita.Dto.ReservaVisitaResponse;
import com.proyecto_backend.reservaVisita.Repository.ReservaVisitaRepository;
import com.proyecto_backend.usuario.Domain.Roles;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import com.proyecto_backend.eventos.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaVisitaServiceTest {

    @Mock
    private ReservaVisitaRepository reservaVisitaRepository;

    @Mock
    private PropiedadRepository propiedadRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ReservaVisitaService reservaVisitaService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crear_deberiaCrearReservaCorrectamente() {
        Long propiedadId = 1L;
        Long clienteId = 2L;
        Long agenteId = 3L;

        ReservaVisitaRequest request = ReservaVisitaRequest.builder()
                .propiedadId(propiedadId)
                .clienteId(clienteId)
                .agenteId(agenteId)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .estado(null) // se espera que se asigne PENDIENTE por defecto
                .comentarios("Comentario de prueba")
                .build();

        Propiedad propiedad = Propiedad.builder().id(propiedadId).build();
        Usuario cliente = Usuario.builder().id(clienteId).rol(Roles.CLIENTE).build();
        Usuario agente = Usuario.builder().id(agenteId).rol(Roles.AGENTE).build();

        when(propiedadRepository.findById(propiedadId)).thenReturn(Optional.of(propiedad));
        when(usuarioRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(agenteId)).thenReturn(Optional.of(agente));
        when(reservaVisitaRepository.save(any(ReservaVisita.class))).thenAnswer(invocation -> {
            ReservaVisita reserva = invocation.getArgument(0);
            reserva.setId(100L);
            return reserva;
        });

        ReservaVisitaResponse response = reservaVisitaService.crear(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(propiedadId, response.getPropiedadId());
        assertEquals(clienteId, response.getClienteId());
        assertEquals(agenteId, response.getAgenteId());
        assertEquals("Comentario de prueba", response.getComentarios());

        verify(emailService, times(1)).enviarCorreoReservaConQR(any(), any());
        verify(emailService, times(1)).enviarCorreoNotificacionAgente(any());
    }

    @Test
    void crear_deberiaLanzarExcepcionSiPropiedadNoExiste() {
        ReservaVisitaRequest request = ReservaVisitaRequest.builder()
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .fechaHora(LocalDateTime.now())
                .build();

        when(propiedadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservaVisitaService.crear(request));
    }

    @Test
    void crear_deberiaLanzarExcepcionSiClienteNoExiste() {
        ReservaVisitaRequest request = ReservaVisitaRequest.builder()
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .fechaHora(LocalDateTime.now())
                .build();

        Propiedad propiedad = Propiedad.builder().id(1L).build();

        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedad));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservaVisitaService.crear(request));
    }

    @Test
    void crear_deberiaLanzarExcepcionSiAgenteNoExiste() {
        ReservaVisitaRequest request = ReservaVisitaRequest.builder()
                .propiedadId(1L)
                .clienteId(2L)
                .agenteId(3L)
                .fechaHora(LocalDateTime.now())
                .build();

        Propiedad propiedad = Propiedad.builder().id(1L).build();
        Usuario cliente = Usuario.builder().id(2L).build();

        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedad));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservaVisitaService.crear(request));
    }

    // Puedes agregar más tests para actualizar, eliminar, confirmar asistencia, etc.
}