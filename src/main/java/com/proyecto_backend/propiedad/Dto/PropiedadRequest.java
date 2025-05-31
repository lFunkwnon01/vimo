package com.proyecto_backend.propiedad.Dto;


import com.proyecto_backend.propiedad.Domain.EstadoPropiedad;
import com.proyecto_backend.propiedad.Domain.TipoPropiedad;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropiedadRequest {

    @NotBlank @Size(max = 100)
    private String titulo;

    @NotBlank @Size(max = 500)
    private String descripcion;

    @NotBlank @Size(max = 200)
    private String direccion;

    @NotNull
    private TipoPropiedad tipo;

    @NotNull @Positive
    private Double metrosCuadrados;

    @NotNull @Positive
    private Double precio;

    @NotNull
    private EstadoPropiedad estado = EstadoPropiedad.DISPONIBLE;

    @NotNull
    private Long propietarioId;

    @Size(max = 1000)
    private String imagenes; // URLs separadas por coma
}
