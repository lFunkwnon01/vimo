package com.proyecto_backend.publicacion.Dto;


import com.proyecto_backend.publicacion.Domain.EstadoPublicacion;
import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicacionResponse {

    private Long id;
    private Long propiedadId;
    private Long creadorId;
    private EstadoPublicacion estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<Long> agentesIds;
    private LocalDateTime creadoEn;
    private TipoTransaccion tipoTransaccion;
}
