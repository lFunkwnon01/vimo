package com.proyecto_backend.propiedad.Dto;


import com.proyecto_backend.propiedad.Domain.EstadoPropiedad;
import com.proyecto_backend.propiedad.Domain.TipoPropiedad;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropiedadResponse {

    private Long id;
    private String titulo;
    private String descripcion;
    private String direccion;
    private TipoPropiedad tipo;
    private Double metrosCuadrados;
    private Double precio;
    private EstadoPropiedad estado;
    private Long propietarioId;
    private String imagenes;
    private Boolean verificada;
    private LocalDateTime creadoEn;
}