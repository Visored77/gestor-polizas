package com.gestor.polizas.controller;

import com.gestor.polizas.dto.RiesgoRequest;
import com.gestor.polizas.dto.RiesgoResponse;
import com.gestor.polizas.service.RiesgoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/riesgos")
public class RiesgoController {

    private final RiesgoService riesgoService;

    public RiesgoController(RiesgoService riesgoService) {
        this.riesgoService = riesgoService;
    }

    @GetMapping
    public List<RiesgoResponse> listar() {
        return riesgoService.listar();
    }

    @GetMapping("/poliza/{polizaId}")
    public List<RiesgoResponse> listarPorPoliza(@PathVariable Integer polizaId) {
        return riesgoService.listarPorPoliza(polizaId);
    }

    @GetMapping("/{id}")
    public RiesgoResponse obtener(@PathVariable Integer id) {
        return riesgoService.obtenerPorId(id);
    }

    @PostMapping("/poliza/{polizaId}")
    public ResponseEntity<RiesgoResponse> crear(@PathVariable Integer polizaId, @Valid @RequestBody RiesgoRequest request) {
        return new ResponseEntity<>(riesgoService.crear(polizaId, request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public RiesgoResponse actualizar(@PathVariable Integer id, @Valid @RequestBody RiesgoRequest request) {
        return riesgoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        riesgoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
