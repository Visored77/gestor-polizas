package com.gestor.polizas.exception;

/**
 * Se lanza cuando una operacion viola una regla de negocio
 * (p. ej. cardinalidad de riesgos, estado invalido para una accion).
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
