package com.proyecto_backend.publicacion.Dto;


import com.proyecto_backend.publicacion.Domain.EstadoPublicacion;
import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicacionRequest {

    @NotNull
    private Long propiedadId;

    @NotNull
    private Long creadorId; // Propietario o admin

    @NotNull
    private EstadoPublicacion estado = EstadoPublicacion.ACTIVA;

    @NotNull
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaFin;

    private TipoTransaccion tipoTransaccion;
}