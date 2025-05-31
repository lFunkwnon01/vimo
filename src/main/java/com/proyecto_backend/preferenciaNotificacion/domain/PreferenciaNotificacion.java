package com.proyecto_backend.preferenciaNotificacion.domain;


import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import com.proyecto_backend.usuario.Domain.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenciaNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // BÚSQUEDA POR UBICACIÓN ESPECÍFICA (lo que quiere el usuario)
    @Column(length = 50)
    private String regionInteres; // "Lima" - donde quiere buscar

    @Column(length = 50)
    private String distritoInteres; // "Barranco, Miraflores" - separados por coma

    // UBICACIÓN DEL USUARIO (donde vive actualmente) - OPCIONAL
    @Column(length = 50)
    private String regionUsuario; // "Junín" - donde vive

    @Column(length = 50)
    private String distritoUsuario; // "Huancayo" - donde vive

    // RADIO SOLO PARA BÚSQUEDA LOCAL (cuando busca cerca de donde vive)
    @Column(name = "radio_km")
    private Double radioKm = 10.0; // Solo para búsqueda local

    @Column(name = "latitud_centro")
    private Double latitudCentro; // Coordenadas del área de interés

    @Column(name = "longitud_centro")
    private Double longitudCentro; // Coordenadas del área de interés

    // TIPO DE BÚSQUEDA
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_busqueda")
    private TipoBusqueda tipoBusqueda = TipoBusqueda.POR_UBICACION;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo; // VENTA, ALQUILER

    @Column(nullable = false)
    private Boolean activa = true;
}