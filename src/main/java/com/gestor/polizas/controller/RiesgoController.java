package com.gestor.polizas.controller;

import com.gestor.polizas.dto.RiesgoResponse;
import com.gestor.polizas.service.RiesgoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/riesgos")
public class RiesgoController {

    private final RiesgoService riesgoService;

    public RiesgoController(RiesgoService riesgoService) {
        this.riesgoService = riesgoService;
    }

    /** 6. Cancelar un riesgo. */
    @PostMapping("/{id}/cancelar")
    public RiesgoResponse cancelar(@PathVariable Integer id) {
        return riesgoService.cancelar(id);
    }
}
