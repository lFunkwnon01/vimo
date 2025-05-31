package com.proyecto_backend.preferenciaNotificacion.dto;

import com.proyecto_backend.preferenciaNotificacion.domain.TipoBusqueda;
import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenciaNotificacionRequest {

    @NotNull
    private Long usuarioId;

    // CAMPOS NUEVOS GEOGRÁFICOS
    private String regionInteres;     // "Lima" - donde quiere buscar
    private String distritoInteres;   // "Barranco,Miraflores" - separados por coma
    private String regionUsuario;     // "Junín" - donde vive (opcional)
    private String distritoUsuario;   // "Huancayo" - donde vive (opcional)

    @Positive
    private Double radioKm;           // Radio de búsqueda en km (opcional, default 10.0)

    private Double latitudCentro;     // Coordenadas del área de interés (opcional)
    private Double longitudCentro;    // Coordenadas del área de interés (opcional)

    private TipoBusqueda tipoBusqueda; // POR_UBICACION, POR_PROXIMIDAD, AMBAS (opcional, default POR_UBICACION)

    @NotNull
    private TipoTransaccion tipo;     // VENTA, ALQUILER

    private Boolean activa;           // Opcional, por defecto true
}