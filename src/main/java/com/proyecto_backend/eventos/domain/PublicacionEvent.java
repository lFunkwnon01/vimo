package com.proyecto_backend.eventos.domain;

import com.proyecto_backend.propiedad.Domain.Propiedad;
import org.springframework.context.ApplicationEvent;

public class PublicacionEvent extends ApplicationEvent {
    private final Propiedad propiedad;
    private final boolean esActualizacion;

    public PublicacionEvent(Object source, Propiedad propiedad, boolean esActualizacion) {
        super(source);
        this.propiedad = propiedad;
        this.esActualizacion = esActualizacion;
    }

    public Propiedad getPropiedad() {
        return propiedad;
    }

    public boolean isEsActualizacion() {
        return esActualizacion;
    }
}