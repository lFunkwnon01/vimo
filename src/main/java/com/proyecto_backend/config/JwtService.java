package com.proyecto_backend.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Domain.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private UsuarioService userService;

    public String extractUsername(String token) {
        return JWT.decode(token).getSubject();
    }

    // Cambiado para aceptar Usuario
    public String generateToken(Usuario usuario){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1000 * 60 * 60 * 10); // 10 horas

        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withSubject(usuario.getEmail())
                .withClaim("role", "ROLE_" + usuario.getRol().name())
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .sign(algorithm);
    }

    public void validateToken(String token, String userEmail) {
        JWT.require(Algorithm.HMAC256(secret)).build().verify(token);

        UserDetails userDetails = userService.loadUserByUsername(userEmail);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}