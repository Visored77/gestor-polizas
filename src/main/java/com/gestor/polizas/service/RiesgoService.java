package com.gestor.polizas.service;

import com.gestor.polizas.dto.RiesgoRequest;
import com.gestor.polizas.dto.RiesgoResponse;
import com.gestor.polizas.entity.Poliza;
import com.gestor.polizas.entity.Riesgo;
import com.gestor.polizas.exception.RecursoNoEncontradoException;
import com.gestor.polizas.mapper.RiesgoMapper;
import com.gestor.polizas.repository.RiesgoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RiesgoService {

    private final RiesgoRepository riesgoRepository;
    private final RiesgoMapper riesgoMapper;
    private final PolizaService polizaService;

    public RiesgoService(RiesgoRepository riesgoRepository, RiesgoMapper riesgoMapper, PolizaService polizaService) {
        this.riesgoRepository = riesgoRepository;
        this.riesgoMapper = riesgoMapper;
        this.polizaService = polizaService;
    }

    @Transactional(readOnly = true)
    public List<RiesgoResponse> listar() {
        return riesgoRepository.findAll().stream()
                .map(riesgoMapper::aResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RiesgoResponse> listarPorPoliza(Integer polizaId) {
        return riesgoRepository.findByPolizaId(polizaId).stream()
                .map(riesgoMapper::aResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RiesgoResponse obtenerPorId(Integer id) {
        return riesgoMapper.aResponse(buscar(id));
    }

    private Riesgo buscar(Integer id) {
        return riesgoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el riesgo con id " + id));
    }

    public RiesgoResponse crear(Integer polizaId, RiesgoRequest request) {
        Poliza poliza = polizaService.buscar(polizaId);
        Riesgo riesgo = riesgoMapper.aEntidad(request);
        riesgo.setPoliza(poliza);
        return riesgoMapper.aResponse(riesgoRepository.save(riesgo));
    }

    public RiesgoResponse actualizar(Integer id, RiesgoRequest request) {
        Riesgo riesgo = buscar(id);
        riesgoMapper.aplicar(riesgo, request);
        return riesgoMapper.aResponse(riesgoRepository.save(riesgo));
    }

    public void eliminar(Integer id) {
        riesgoRepository.delete(buscar(id));
    }
}
