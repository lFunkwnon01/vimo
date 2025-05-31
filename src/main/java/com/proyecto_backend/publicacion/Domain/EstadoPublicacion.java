package com.proyecto_backend.publicacion.Domain;

public enum EstadoPublicacion {
    BORRADOR,     // Creada pero no publicada
    ACTIVA,       // Visible para agentes y clientes
    PAUSADA,      // Temporalmente inactiva
    CERRADA,      // Transacción completada
    EXPIRADA,     // Venció por tiempo
    FINALIZADA     // Cancelada por propietario
}