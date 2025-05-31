package com.proyecto_backend.reservaVisita.Dto;


import com.proyecto_backend.reservaVisita.Domain.EstadoReservaVisita;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaVisitaRequest {

    @NotNull
    private Long propiedadId;

    @NotNull
    private Long clienteId;

    @NotNull
    private Long agenteId;

    @NotNull
    private LocalDateTime fechaHora;

    @NotNull
    private EstadoReservaVisita estado = EstadoReservaVisita.PENDIENTE;

    @Size(max = 500)
    private String comentarios;
}
