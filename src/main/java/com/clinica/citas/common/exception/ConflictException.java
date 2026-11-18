package com.clinica.citas.common.exception;

/** Regla de negocio incumplida: DNI duplicado, cita solapada, etc. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
