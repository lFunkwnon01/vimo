package com.proyecto_backend.chatMensaje.Domain;


import com.proyecto_backend.propiedad.Domain.Propiedad;
import com.proyecto_backend.usuario.Domain.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario que envía el mensaje (cliente o agente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente;

    // Usuario que recibe el mensaje (cliente o agente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Usuario destinatario;

    // Propiedad sobre la que se conversa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propiedad_id", nullable = false)
    private Propiedad propiedad;

    // Agente relacionado al chat (para identificar el contexto)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agente_id", nullable = false)
    private Usuario agente;

    @Column(nullable = false, length = 200)
    private String titulo; // Ejemplo: "Consulta sobre Propiedad X"

    @Column(nullable = false, length = 1000)
    private String mensaje;

    @Column(nullable = false)
    private Boolean leido = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime enviadoEn;
}