package com.gestor.polizas.controller;

import com.gestor.polizas.dto.AuditoriaCoreRequest;
import com.gestor.polizas.dto.AuditoriaCoreResponse;
import com.gestor.polizas.service.AuditoriaCoreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditorias")
public class AuditoriaCoreController {

    private final AuditoriaCoreService auditoriaCoreService;

    public AuditoriaCoreController(AuditoriaCoreService auditoriaCoreService) {
        this.auditoriaCoreService = auditoriaCoreService;
    }

    @GetMapping
    public List<AuditoriaCoreResponse> listar() {
        return auditoriaCoreService.listar();
    }

    @GetMapping("/{id}")
    public AuditoriaCoreResponse obtener(@PathVariable Integer id) {
        return auditoriaCoreService.obtenerPorId(id);
    }

    @GetMapping("/poliza/{polizaId}")
    public List<AuditoriaCoreResponse> listarPorPoliza(@PathVariable Integer polizaId) {
        return auditoriaCoreService.listarPorPoliza(polizaId);
    }

    @PostMapping
    public ResponseEntity<AuditoriaCoreResponse> crear(@Valid @RequestBody AuditoriaCoreRequest request) {
        return new ResponseEntity<>(auditoriaCoreService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public AuditoriaCoreResponse actualizar(@PathVariable Integer id, @Valid @RequestBody AuditoriaCoreRequest request) {
        return auditoriaCoreService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        auditoriaCoreService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
