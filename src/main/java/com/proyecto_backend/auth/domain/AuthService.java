package com.proyecto_backend.auth.domain;

import com.proyecto_backend.Excepctions.UserAlreadyExistException;
import com.proyecto_backend.auth.dto.JwtAuthResponse;
import com.proyecto_backend.auth.dto.LoginRequest;
import com.proyecto_backend.auth.dto.RegisterRequest;
import com.proyecto_backend.config.JwtService;
import com.proyecto_backend.usuario.Domain.Roles;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper = new ModelMapper();

    public JwtAuthResponse login(LoginRequest req) {
        Optional<Usuario> user = userRepository.findByEmail(req.getEmail());

        if (user.isEmpty()) throw new UsernameNotFoundException("Email is not registered");

        if (!passwordEncoder.matches(req.getPassword(), user.get().getPasswordHash()))
            throw new IllegalArgumentException("Password is incorrect");

        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(jwtService.generateToken(user.get()));
        return response;
    }

    public JwtAuthResponse register(RegisterRequest req){
        Optional<Usuario> user = userRepository.findByEmail(req.getEmail());
        if (user.isPresent()) throw new UserAlreadyExistException("Email is already registered");

        Usuario newUser = new Usuario();
        newUser.setNombre(req.getName());
        newUser.setApellido(req.getApellido()); // o ajusta según tu DTO
        newUser.setEmail(req.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        newUser.setTelefono(req.getPhone());
        newUser.setRol(Roles.CLIENTE); // rol por defecto
        newUser.setActivo(true);
        newUser.setVerificado(false);
        newUser.setCreadoEn(ZonedDateTime.now().toLocalDateTime());

        userRepository.save(newUser);

        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(jwtService.generateToken(newUser));
        return response;
    }
}