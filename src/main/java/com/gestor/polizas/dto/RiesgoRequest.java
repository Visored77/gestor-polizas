package com.gestor.polizas.dto;

import com.gestor.polizas.entity.enums.EstadoRiesgo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Datos de entrada para crear o actualizar un riesgo (inmueble amparado).
 */
public record RiesgoRequest(

        @NotBlank(message = "La direccion del inmueble es obligatoria")
        String inmuebleDireccion,

        @NotBlank(message = "El nombre del arrendatario es obligatorio")
        String arrendatarioNombre,

        @NotBlank(message = "El documento del arrendatario es obligatorio")
        String arrendatarioDocumento,

        @NotNull(message = "El canon individual es obligatorio")
        @Positive(message = "El canon individual debe ser mayor que cero")
        BigDecimal canonIndividual,

        EstadoRiesgo estado
) {
}
