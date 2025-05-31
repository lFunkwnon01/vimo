package com.proyecto_backend.chatMensaje.Domain;


import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.chatMensaje.Dto.ChatMensajeRequest;
import com.proyecto_backend.chatMensaje.Dto.ChatMensajeResponse;
import com.proyecto_backend.chatMensaje.Repository.ChatMensajeRepository;
import com.proyecto_backend.propiedad.Domain.Propiedad;
import com.proyecto_backend.propiedad.Repository.PropiedadRepository;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMensajeService {

    private final ChatMensajeRepository chatMensajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final PropiedadRepository propiedadRepository;

    public ChatMensajeResponse crear(ChatMensajeRequest dto) {
        Usuario remitente = usuarioRepository.findById(dto.getRemitenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (remitente)", dto.getRemitenteId()));

        Usuario destinatario = usuarioRepository.findById(dto.getDestinatarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (destinatario)", dto.getDestinatarioId()));

        Usuario agente = usuarioRepository.findById(dto.getAgenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (agente)", dto.getAgenteId()));

        Propiedad propiedad = propiedadRepository.findById(dto.getPropiedadId())
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", dto.getPropiedadId()));

        ChatMensaje chatMensaje = mapToEntity(dto, remitente, destinatario, agente, propiedad);

        return mapToResponse(chatMensajeRepository.save(chatMensaje));
    }

    public ChatMensajeResponse obtener(Long id) {
        ChatMensaje c = chatMensajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMensaje", id));
        return mapToResponse(c);
    }

    public List<ChatMensajeResponse> listar() {
        return chatMensajeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void eliminar(Long id) {
        if (!chatMensajeRepository.existsById(id))
            throw new ResourceNotFoundException("ChatMensaje", id);
        chatMensajeRepository.deleteById(id);
    }

    // Métodos personalizados

    public List<ChatMensajeResponse> listarPorPropiedadYAgente(Long propiedadId, Long agenteId) {
        return chatMensajeRepository.findByPropiedadIdAndAgenteIdOrderByEnviadoEnAsc(propiedadId, agenteId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ChatMensajeResponse> listarPorClienteYPropiedad(Long clienteId, Long propiedadId) {
        return chatMensajeRepository.findByRemitenteIdAndPropiedadIdOrderByEnviadoEnAsc(clienteId, propiedadId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ChatMensajeResponse> listarPorAgente(Long agenteId) {
        return chatMensajeRepository.findByAgenteIdOrderByEnviadoEnAsc(agenteId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ChatMensajeResponse marcarComoLeido(Long id) {
        ChatMensaje c = chatMensajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMensaje", id));
        c.setLeido(true);
        return mapToResponse(chatMensajeRepository.save(c));
    }

    // Mapeos

    private ChatMensaje mapToEntity(ChatMensajeRequest d, Usuario remitente, Usuario destinatario, Usuario agente, Propiedad propiedad) {
        return ChatMensaje.builder()
                .remitente(remitente)
                .destinatario(destinatario)
                .agente(agente)
                .propiedad(propiedad)
                .titulo(d.getTitulo())
                .mensaje(d.getMensaje())
                .leido(false)
                .build();
    }

    private ChatMensajeResponse mapToResponse(ChatMensaje c) {
        return ChatMensajeResponse.builder()
                .id(c.getId())
                .remitenteId(c.getRemitente().getId())
                .destinatarioId(c.getDestinatario().getId())
                .agenteId(c.getAgente().getId())
                .propiedadId(c.getPropiedad().getId())
                .titulo(c.getTitulo())
                .mensaje(c.getMensaje())
                .leido(c.getLeido())
                .enviadoEn(c.getEnviadoEn())
                .build();
    }
}
