package com.proyecto_backend.preferenciaNotificacion.dto;

import com.proyecto_backend.preferenciaNotificacion.domain.TipoBusqueda;
import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenciaNotificacionResponse {

    private Long id;
    private Long usuarioId;

    // CAMPOS GEOGRÁFICOS
    private String regionInteres;
    private String distritoInteres;
    private String regionUsuario;
    private String distritoUsuario;
    private Double radioKm;
    private Double latitudCentro;
    private Double longitudCentro;
    private TipoBusqueda tipoBusqueda;

    private TipoTransaccion tipo;
    private Boolean activa;
}