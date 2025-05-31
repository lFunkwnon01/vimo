package com.proyecto_backend.reservaVisita.Dto;

import com.proyecto_backend.reservaVisita.Domain.EstadoReservaVisita;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaVisitaResponse {

    private Long id;
    private Long propiedadId;
    private Long clienteId;
    private Long agenteId;
    private LocalDateTime fechaHora;
    private EstadoReservaVisita estado;
    private String comentarios;
    private LocalDateTime creadoEn;
}
