package com.gestor.polizas.dto;

import com.gestor.polizas.entity.enums.EstadoRiesgo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Datos de salida de un riesgo. Incluye polizaId en lugar de la entidad completa
 * para evitar ciclos de serializacion.
 */
public record RiesgoResponse(
        Integer id,
        Integer polizaId,
        String inmuebleDireccion,
        String arrendatarioNombre,
        String arrendatarioDocumento,
        BigDecimal canonIndividual,
        EstadoRiesgo estado,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
