package com.proyecto_backend.chatMensaje.Repository;



import com.proyecto_backend.chatMensaje.Domain.ChatMensaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMensajeRepository extends JpaRepository<ChatMensaje, Long> {

    // Mensajes de un chat específico (por propiedad y agente)
    List<ChatMensaje> findByPropiedadIdAndAgenteIdOrderByEnviadoEnAsc(Long propiedadId, Long agenteId);

    // Mensajes enviados por un cliente sobre una propiedad
    List<ChatMensaje> findByRemitenteIdAndPropiedadIdOrderByEnviadoEnAsc(Long remitenteId, Long propiedadId);

    // Todos los mensajes de un agente
    List<ChatMensaje> findByAgenteIdOrderByEnviadoEnAsc(Long agenteId);
}