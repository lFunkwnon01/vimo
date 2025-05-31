package com.proyecto_backend.usuario.Dto;



import com.proyecto_backend.usuario.Domain.Roles;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRequest {

    @NotBlank @Size(max = 60)
    private String nombre;

    @NotBlank @Size(max = 60)
    private String apellido;

    @Email @NotBlank @Size(max = 120)
    private String email;

    @NotBlank @Size(min = 6)
    private String password;

    @Size(max = 20)
    private String telefono;

    private Roles rol = Roles.CLIENTE;

    // Opcional: para registro de agentes/propietarios
    private String documentoVerificacion;
}