package com.gestor.polizas.controller;

import com.gestor.polizas.dto.PolizaRequest;
import com.gestor.polizas.dto.PolizaResponse;
import com.gestor.polizas.dto.RenovacionRequest;
import com.gestor.polizas.dto.RiesgoRequest;
import com.gestor.polizas.dto.RiesgoResponse;
import com.gestor.polizas.entity.EstadoPoliza;
import com.gestor.polizas.entity.TipoPoliza;
import com.gestor.polizas.service.PolizaService;
import com.gestor.polizas.service.RiesgoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
public class PolizaController {

    private final PolizaService polizaService;
    private final RiesgoService riesgoService;

    public PolizaController(PolizaService polizaService, RiesgoService riesgoService) {
        this.polizaService = polizaService;
        this.riesgoService = riesgoService;
    }

    /** 1. Listar polizas, filtrando opcionalmente por tipo y estado. */
    @GetMapping
    public List<PolizaResponse> listar(@RequestParam(required = false) TipoPoliza tipo,
                                       @RequestParam(required = false) EstadoPoliza estado) {
        return polizaService.listar(tipo, estado);
    }

    @GetMapping("/{id}")
    public PolizaResponse obtener(@PathVariable Integer id) {
        return polizaService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<PolizaResponse> crear(@Valid @RequestBody PolizaRequest request) {
        return new ResponseEntity<>(polizaService.crear(request), HttpStatus.CREATED);
    }

    /** 2. Listar los riesgos de una poliza. */
    @GetMapping("/{id}/riesgos")
    public List<RiesgoResponse> listarRiesgos(@PathVariable Integer id) {
        return riesgoService.listarPorPoliza(id);
    }

    /** 5. Agregar un riesgo a una poliza (solo si es Colectiva). */
    @PostMapping("/{id}/riesgos")
    public ResponseEntity<RiesgoResponse> agregarRiesgo(@PathVariable Integer id,
                                                        @Valid @RequestBody RiesgoRequest request) {
        return new ResponseEntity<>(riesgoService.crear(id, request), HttpStatus.CREATED);
    }

    /** 3. Renovar una poliza (incrementa canon y prima por IPC; estado -> RENOVADA). */
    @PostMapping("/{id}/renovar")
    public PolizaResponse renovar(@PathVariable Integer id, @Valid @RequestBody RenovacionRequest request) {
        return polizaService.renovar(id, request);
    }

    /** 4. Cancelar una poliza (cancela en cascada todos sus riesgos). */
    @PostMapping("/{id}/cancelar")
    public PolizaResponse cancelar(@PathVariable Integer id) {
        return polizaService.cancelar(id);
    }
}
