package com.proyecto_backend.transaccion.Domain;



import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.eventos.domain.TransaccionCompletadaEvent;
import com.proyecto_backend.propiedad.Domain.Propiedad;
import com.proyecto_backend.propiedad.Repository.PropiedadRepository;
import com.proyecto_backend.transaccion.Dto.TransaccionRequest;
import com.proyecto_backend.transaccion.Dto.TransaccionResponse;
import com.proyecto_backend.transaccion.Repository.TransaccionRepository;
import com.proyecto_backend.usuario.Domain.Roles;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final PropiedadRepository propiedadRepository;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TransaccionResponse crear(TransaccionRequest dto) {
        Propiedad propiedad = propiedadRepository.findById(dto.getPropiedadId())
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", dto.getPropiedadId()));

        Usuario cliente = usuarioRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (cliente)", dto.getClienteId()));

        Usuario agente = usuarioRepository.findById(dto.getAgenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (agente)", dto.getAgenteId()));

        if (cliente.getRol() != Roles.CLIENTE) {
            throw new IllegalArgumentException("Solo usuarios con rol CLIENTE pueden ser clientes en una transacción.");
        }
        if (agente.getRol() != Roles.AGENTE) {
            throw new IllegalArgumentException("Solo usuarios con rol AGENTE pueden ser agentes en una transacción.");
        }

        Transaccion transaccion = mapToEntity(dto, propiedad, cliente, agente);

        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);

        // Lanzar evento de transacción completada
        eventPublisher.publishEvent(new TransaccionCompletadaEvent(this, transaccionGuardada));

        return mapToResponse(transaccionGuardada);
    }

    public TransaccionResponse obtener(Long id) {
        Transaccion t = transaccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaccion", id));
        return mapToResponse(t);
    }

    public List<TransaccionResponse> listar() {
        return transaccionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TransaccionResponse actualizar(Long id, TransaccionRequest dto) {
        Transaccion t = transaccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaccion", id));

        Propiedad propiedad = propiedadRepository.findById(dto.getPropiedadId())
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", dto.getPropiedadId()));

        Usuario cliente = usuarioRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (cliente)", dto.getClienteId()));

        Usuario agente = usuarioRepository.findById(dto.getAgenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (agente)", dto.getAgenteId()));

        t.setPropiedad(propiedad);
        t.setCliente(cliente);
        t.setAgente(agente);
        t.setTipo(dto.getTipo());
        t.setMonto(dto.getMonto());
        t.setComisionAgente(dto.getComisionAgente());
        t.setDetalles(dto.getDetalles());

        return mapToResponse(transaccionRepository.save(t));
    }

    public void eliminar(Long id) {
        if (!transaccionRepository.existsById(id))
            throw new ResourceNotFoundException("Transaccion", id);
        transaccionRepository.deleteById(id);
    }

    // Métodos personalizados

    public List<TransaccionResponse> listarPorCliente(Long clienteId) {
        return transaccionRepository.findByClienteId(clienteId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TransaccionResponse> listarPorAgente(Long agenteId) {
        return transaccionRepository.findByAgenteId(agenteId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TransaccionResponse> listarPorPropiedad(Long propiedadId) {
        return transaccionRepository.findByPropiedadId(propiedadId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TransaccionResponse> listarPorAgenteYMes(Long agenteId, int anio, int mes) {
        return transaccionRepository.findByAgenteIdAndFechaBetween(
                agenteId,
                java.time.LocalDateTime.of(anio, mes, 1, 0, 0),
                java.time.LocalDateTime.of(anio, mes, java.time.Month.of(mes).length(java.time.Year.isLeap(anio)), 23, 59, 59)
        ).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Mapeos

    private Transaccion mapToEntity(TransaccionRequest d, Propiedad propiedad, Usuario cliente, Usuario agente) {
        return Transaccion.builder()
                .propiedad(propiedad)
                .cliente(cliente)
                .agente(agente)
                .tipo(d.getTipo())
                .monto(d.getMonto())
                .comisionAgente(d.getComisionAgente())
                .detalles(d.getDetalles())
                .build();
    }

    private TransaccionResponse mapToResponse(Transaccion t) {
        return TransaccionResponse.builder()
                .id(t.getId())
                .propiedadId(t.getPropiedad().getId())
                .clienteId(t.getCliente().getId())
                .agenteId(t.getAgente().getId())
                .tipo(t.getTipo())
                .monto(t.getMonto())
                .comisionAgente(t.getComisionAgente())
                .fecha(t.getFecha())
                .detalles(t.getDetalles())
                .build();
    }
}