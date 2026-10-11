package com.gestor.polizas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Evento recibido por el mock del CORE.
 * Ejemplo: {"evento":"ACTUALIZACION","polizaId":555}
 */
public record EventoCoreRequest(

        @NotBlank(message = "El evento es obligatorio")
        String evento,

        @NotNull(message = "El polizaId es obligatorio")
        Integer polizaId
) {
}
