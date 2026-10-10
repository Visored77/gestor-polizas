package com.gestor.polizas.service;

import com.gestor.polizas.dto.AuditoriaCoreRequest;
import com.gestor.polizas.dto.AuditoriaCoreResponse;
import com.gestor.polizas.entity.AuditoriaCore;
import com.gestor.polizas.exception.RecursoNoEncontradoException;
import com.gestor.polizas.mapper.AuditoriaCoreMapper;
import com.gestor.polizas.repository.AuditoriaCoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuditoriaCoreService {

    private final AuditoriaCoreRepository auditoriaCoreRepository;
    private final AuditoriaCoreMapper auditoriaCoreMapper;
    private final PolizaService polizaService;

    public AuditoriaCoreService(AuditoriaCoreRepository auditoriaCoreRepository,
                                AuditoriaCoreMapper auditoriaCoreMapper,
                                PolizaService polizaService) {
        this.auditoriaCoreRepository = auditoriaCoreRepository;
        this.auditoriaCoreMapper = auditoriaCoreMapper;
        this.polizaService = polizaService;
    }

    @Transactional(readOnly = true)
    public List<AuditoriaCoreResponse> listar() {
        return auditoriaCoreRepository.findAll().stream()
                .map(auditoriaCoreMapper::aResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AuditoriaCoreResponse obtenerPorId(Integer id) {
        return auditoriaCoreMapper.aResponse(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<AuditoriaCoreResponse> listarPorPoliza(Integer polizaId) {
        return auditoriaCoreRepository.findByPolizaId(polizaId).stream()
                .map(auditoriaCoreMapper::aResponse)
                .toList();
    }

    private AuditoriaCore buscar(Integer id) {
        return auditoriaCoreRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el registro de auditoria con id " + id));
    }

    public AuditoriaCoreResponse crear(AuditoriaCoreRequest request) {
        AuditoriaCore auditoria = auditoriaCoreMapper.aEntidad(request);
        if (request.polizaId() != null) {
            auditoria.setPoliza(polizaService.buscar(request.polizaId()));
        }
        return auditoriaCoreMapper.aResponse(auditoriaCoreRepository.save(auditoria));
    }

    public AuditoriaCoreResponse actualizar(Integer id, AuditoriaCoreRequest request) {
        AuditoriaCore auditoria = buscar(id);
        auditoriaCoreMapper.aplicar(auditoria, request);
        if (request.polizaId() != null) {
            auditoria.setPoliza(polizaService.buscar(request.polizaId()));
        }
        return auditoriaCoreMapper.aResponse(auditoriaCoreRepository.save(auditoria));
    }

    public void eliminar(Integer id) {
        auditoriaCoreRepository.delete(buscar(id));
    }
}
