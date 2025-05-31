package com.proyecto_backend.transaccion.Dto;

import com.proyecto_backend.transaccion.Domain.TipoTransaccion;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionRequest {

    @NotNull
    private Long propiedadId;

    @NotNull
    private Long clienteId;

    @NotNull
    private Long agenteId;

    @NotNull
    private TipoTransaccion tipo;

    @NotNull @Positive
    private Double monto;

    @NotNull @PositiveOrZero
    private Double comisionAgente;

    @Size(max = 500)
    private String detalles;
}