package com.proyecto_backend.ubicaciones.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UbicacionGeografica {

    @Column(name = "direccion_completa", length = 300)
    private String direccionCompleta;

    @Column(name = "region", length = 50)
    private String region; // Lima, Junín, Loreto

    @Column(name = "provincia", length = 50)
    private String provincia; // Lima, Huancayo, Maynas

    @Column(name = "distrito", length = 50)
    private String distrito; // Barranco, El Tambo, Iquitos

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    @Column(name = "pais", length = 50)
    private String pais = "Perú";
}