package com.proyecto_backend.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String apellido;
    private String email;
    private String password;
    private String phone;
}