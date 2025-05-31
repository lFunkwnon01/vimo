package com.proyecto_backend.transaccion.Dto;

import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionResponse {

    private Long id;
    private Long propiedadId;
    private Long clienteId;
    private Long agenteId;
    private TipoTransaccion tipo;
    private Double monto;
    private Double comisionAgente;
    private LocalDateTime fecha;
    private String detalles;
}