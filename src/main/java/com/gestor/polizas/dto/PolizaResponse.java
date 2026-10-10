package com.gestor.polizas.dto;

import com.gestor.polizas.entity.EstadoPoliza;
import com.gestor.polizas.entity.TipoPoliza;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Datos de salida de una poliza.
 */
public record PolizaResponse(
        Integer id,
        TipoPoliza tipoPoliza,
        String tomadorNombre,
        String tomadorDocumento,
        String aseguradoNombre,
        String aseguradoDocumento,
        String beneficiarioNombre,
        String beneficiarioDocumento,
        LocalDate vigenciaInicio,
        LocalDate vigenciaFin,
        BigDecimal canonMensual,
        Integer mesesVigencia,
        BigDecimal valorPrima,
        EstadoPoliza estado,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
