package com.gestor.polizas.dto;

import com.gestor.polizas.entity.EstadoSincronizacion;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos de entrada para crear o actualizar un registro de auditoria con el CORE.
 */
public record AuditoriaCoreRequest(

        Integer polizaId,

        @NotBlank(message = "La accion es obligatoria")
        String accion,

        @NotBlank(message = "El payload enviado es obligatorio")
        String payloadEnviado,

        EstadoSincronizacion estadoSincronizacion,

        Integer intentos,

        String respuestaCore
) {
}
