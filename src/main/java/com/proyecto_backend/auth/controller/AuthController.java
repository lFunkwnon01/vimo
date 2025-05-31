package com.proyecto_backend.auth.controller;

import com.proyecto_backend.auth.domain.AuthService;
import com.proyecto_backend.auth.dto.JwtAuthResponse;
import com.proyecto_backend.auth.dto.LoginRequest;
import com.proyecto_backend.auth.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest loginReq) {
        return ResponseEntity.ok(authService.login(loginReq));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> register(@RequestBody RegisterRequest req){
        return ResponseEntity.ok(authService.register(req));
    }
}