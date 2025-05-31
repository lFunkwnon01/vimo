package com.proyecto_backend.chatMensaje.Controller;


import com.proyecto_backend.chatMensaje.Domain.ChatMensajeService;
import com.proyecto_backend.chatMensaje.Dto.ChatMensajeRequest;
import com.proyecto_backend.chatMensaje.Dto.ChatMensajeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-mensajes")
@RequiredArgsConstructor
public class ChatMensajeController {

    private final ChatMensajeService chatMensajeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMensajeResponse crear(@Valid @RequestBody ChatMensajeRequest dto) {
        return chatMensajeService.crear(dto);
    }

    @GetMapping("/{id}")
    public ChatMensajeResponse obtener(@PathVariable Long id) {
        return chatMensajeService.obtener(id);
    }

    @GetMapping
    public List<ChatMensajeResponse> listar() {
        return chatMensajeService.listar();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        chatMensajeService.eliminar(id);
    }

    // Métodos personalizados

    @GetMapping("/por-propiedad-agente")
    public List<ChatMensajeResponse> listarPorPropiedadYAgente(
            @RequestParam Long propiedadId,
            @RequestParam Long agenteId
    ) {
        return chatMensajeService.listarPorPropiedadYAgente(propiedadId, agenteId);
    }

    @GetMapping("/por-cliente-propiedad")
    public List<ChatMensajeResponse> listarPorClienteYPropiedad(
            @RequestParam Long clienteId,
            @RequestParam Long propiedadId
    ) {
        return chatMensajeService.listarPorClienteYPropiedad(clienteId, propiedadId);
    }

    @GetMapping("/por-agente/{agenteId}")
    public List<ChatMensajeResponse> listarPorAgente(@PathVariable Long agenteId) {
        return chatMensajeService.listarPorAgente(agenteId);
    }

    @PatchMapping("/{id}/leido")
    public ChatMensajeResponse marcarComoLeido(@PathVariable Long id) {
        return chatMensajeService.marcarComoLeido(id);
    }
}
