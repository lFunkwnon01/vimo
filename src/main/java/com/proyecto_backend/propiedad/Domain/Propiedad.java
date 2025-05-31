package com.proyecto_backend.propiedad.Domain;



import com.proyecto_backend.ubicaciones.domain.UbicacionGeografica;
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
public class Propiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Embedded
    private UbicacionGeografica ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPropiedad tipo;

    @Column(nullable = false)
    private Double metrosCuadrados;

    @Column(nullable = false)
    private Double precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPropiedad estado = EstadoPropiedad.DISPONIBLE;

    // Relación: una propiedad tiene un propietario (usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;

    // Imágenes: URLs separadas por coma
    @Column(length = 1000)
    private String imagenes;

    // Verificación de propiedad
    @Column(nullable = false)
    private Boolean verificada = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creadoEn;
}