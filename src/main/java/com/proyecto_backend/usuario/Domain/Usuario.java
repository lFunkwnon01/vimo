package com.proyecto_backend.usuario.Domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false, length = 60)
    private String apellido;

    @Column(unique = true, nullable = false, length = 120)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles rol = Roles.CLIENTE;

    @Column(nullable = false)
    private Boolean activo = true;

    // Para verificación de agentes/propietarios
    @Column(nullable = false)
    private Boolean verificado = false;

    // Si quieres guardar la URL o nombre del archivo de verificación
    @Column(length = 255)
    private String documentoVerificacion;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creadoEn;
}
