package com.gestor.polizas.controller;

import com.gestor.polizas.dto.EventoCoreRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Mock del CORE transaccional legado. Su unico proposito es registrar en logs
 * que la operacion se intento enviar al CORE.
 */
@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    private static final Logger log = LoggerFactory.getLogger(CoreMockController.class);

    @PostMapping("/evento")
    public Map<String, Object> recibirEvento(@Valid @RequestBody EventoCoreRequest request) {
        log.info("[CORE-MOCK] Intento de envio al CORE recibido: evento={} polizaId={}",
                request.evento(), request.polizaId());
        return Map.of(
                "recibido", true,
                "evento", request.evento(),
                "polizaId", request.polizaId(),
                "mensaje", "Evento registrado en logs (mock CORE)"
        );
    }
}
