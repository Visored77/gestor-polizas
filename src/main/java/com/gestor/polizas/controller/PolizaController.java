package com.gestor.polizas.controller;

import com.gestor.polizas.dto.PolizaRequest;
import com.gestor.polizas.dto.PolizaResponse;
import com.gestor.polizas.service.PolizaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/polizas")
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    @GetMapping
    public List<PolizaResponse> listar() {
        return polizaService.listar();
    }

    @GetMapping("/{id}")
    public PolizaResponse obtener(@PathVariable Integer id) {
        return polizaService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<PolizaResponse> crear(@Valid @RequestBody PolizaRequest request) {
        return new ResponseEntity<>(polizaService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public PolizaResponse actualizar(@PathVariable Integer id, @Valid @RequestBody PolizaRequest request) {
        return polizaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        polizaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
