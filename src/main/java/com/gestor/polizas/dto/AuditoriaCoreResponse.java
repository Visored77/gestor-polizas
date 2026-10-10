package com.gestor.polizas.dto;

import com.gestor.polizas.entity.EstadoSincronizacion;

import java.time.LocalDateTime;

/**
 * Datos de salida de un registro de auditoria con el CORE.
 */
public record AuditoriaCoreResponse(
        Integer id,
        Integer polizaId,
        String accion,
        String payloadEnviado,
        EstadoSincronizacion estadoSincronizacion,
        Integer intentos,
        String respuestaCore,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
