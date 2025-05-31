package com.proyecto_backend.chatMensaje.Dto;import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMensajeResponse {

    private Long id;
    private Long remitenteId;
    private Long destinatarioId;
    private Long agenteId;
    private Long propiedadId;
    private String titulo;
    private String mensaje;
    private Boolean leido;
    private LocalDateTime enviadoEn;
}
