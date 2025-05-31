package com.proyecto_backend.eventos.domain;

import com.proyecto_backend.usuario.Domain.Usuario;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class VerificacionSolicitadaEvent extends ApplicationEvent {

    private final Usuario usuario;

    public VerificacionSolicitadaEvent(Object source, Usuario usuario) {
        super(source);
        this.usuario = usuario;
    }
}