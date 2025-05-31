package com.proyecto_backend.transaccion.Domain;


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
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con propiedad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propiedad_id", nullable = false)
    private Propiedad propiedad;

    // Relación con cliente (quien paga)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    // Relación con agente (quien cierra la transacción)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agente_id", nullable = false)
    private Usuario agente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private Double comisionAgente;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fecha;

    @Column(length = 500)
    private String detalles;
}
