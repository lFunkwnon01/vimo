package com.proyecto_backend.usuario.Dto;



import com.proyecto_backend.usuario.Domain.Roles;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private Roles rol;
    private Boolean activo;
    private Boolean verificado;
    private String documentoVerificacion;
    private LocalDateTime creadoEn;
}