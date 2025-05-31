package com.proyecto_backend.chatMensaje.Dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMensajeRequest {

    @NotNull
    private Long remitenteId;

    @NotNull
    private Long destinatarioId;

    @NotNull
    private Long agenteId;

    @NotNull
    private Long propiedadId;

    @NotBlank @Size(max = 200)
    private String titulo;

    @NotBlank @Size(max = 1000)
    private String mensaje;
}
