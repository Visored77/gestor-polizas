package com.gestor.polizas.dto;

import com.gestor.polizas.entity.enums.EstadoPoliza;
import com.gestor.polizas.entity.enums.TipoPoliza;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos de entrada para crear o actualizar una poliza.
 */
public record PolizaRequest(

        @NotNull(message = "El tipo de poliza es obligatorio")
        TipoPoliza tipoPoliza,

        @NotBlank(message = "El nombre del tomador es obligatorio")
        String tomadorNombre,

        @NotBlank(message = "El documento del tomador es obligatorio")
        String tomadorDocumento,

        @NotBlank(message = "El nombre del asegurado es obligatorio")
        String aseguradoNombre,

        @NotBlank(message = "El documento del asegurado es obligatorio")
        String aseguradoDocumento,

        @NotBlank(message = "El nombre del beneficiario es obligatorio")
        String beneficiarioNombre,

        @NotBlank(message = "El documento del beneficiario es obligatorio")
        String beneficiarioDocumento,

        @NotNull(message = "La fecha de inicio de vigencia es obligatoria")
        LocalDate vigenciaInicio,

        @NotNull(message = "La fecha de fin de vigencia es obligatoria")
        LocalDate vigenciaFin,

        @NotNull(message = "El canon mensual es obligatorio")
        @Positive(message = "El canon mensual debe ser mayor que cero")
        BigDecimal canonMensual,

        @NotNull(message = "Los meses de vigencia son obligatorios")
        @Positive(message = "Los meses de vigencia deben ser mayor que cero")
        Integer mesesVigencia,

        EstadoPoliza estado
) {
}
