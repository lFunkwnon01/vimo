package com.proyecto_backend.eventos.domain;

import com.proyecto_backend.reservaVisita.Domain.ReservaVisita;
import org.springframework.context.ApplicationEvent;

public class VisitaConfirmadaEvent extends ApplicationEvent {
    private final ReservaVisita reservaVisita;

    public VisitaConfirmadaEvent(Object source, ReservaVisita reservaVisita) {
        super(source);
        this.reservaVisita = reservaVisita;
    }

    public ReservaVisita getReservaVisita() {
        return reservaVisita;
    }
}