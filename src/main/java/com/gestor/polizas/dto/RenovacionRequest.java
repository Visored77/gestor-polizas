package com.gestor.polizas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * Datos para renovar una poliza. El canon se ajusta segun el incremento del IPC.
 * Ejemplo: ipcPorcentaje = 7.5 representa un incremento del 7.5%.
 */
public record RenovacionRequest(

        @NotNull(message = "El porcentaje de IPC es obligatorio")
        @PositiveOrZero(message = "El porcentaje de IPC no puede ser negativo")
        BigDecimal ipcPorcentaje
) {
}
