package com.proyecto_backend.eventos.domain;

import com.proyecto_backend.transaccion.Domain.Transaccion;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TransaccionCompletadaEvent extends ApplicationEvent {

    private final Transaccion transaccion;

    public TransaccionCompletadaEvent(Object source, Transaccion transaccion) {
        super(source);
        this.transaccion = transaccion;
    }
}