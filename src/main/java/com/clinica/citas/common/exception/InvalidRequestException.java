package com.clinica.citas.common.exception;

/** Petición con datos coherentes en formato pero inválidos por negocio (p. ej. fin antes que inicio). */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
