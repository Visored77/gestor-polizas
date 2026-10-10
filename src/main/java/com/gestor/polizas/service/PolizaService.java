package com.gestor.polizas.service;

import com.gestor.polizas.dto.PolizaRequest;
import com.gestor.polizas.dto.PolizaResponse;
import com.gestor.polizas.entity.Poliza;
import com.gestor.polizas.exception.RecursoNoEncontradoException;
import com.gestor.polizas.mapper.PolizaMapper;
import com.gestor.polizas.repository.PolizaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final PolizaMapper polizaMapper;

    public PolizaService(PolizaRepository polizaRepository, PolizaMapper polizaMapper) {
        this.polizaRepository = polizaRepository;
        this.polizaMapper = polizaMapper;
    }

    @Transactional(readOnly = true)
    public List<PolizaResponse> listar() {
        return polizaRepository.findAll().stream()
                .map(polizaMapper::aResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PolizaResponse obtenerPorId(Integer id) {
        return polizaMapper.aResponse(buscar(id));
    }

    /** Devuelve la entidad gestionada; util para otros servicios. */
    @Transactional(readOnly = true)
    public Poliza buscar(Integer id) {
        return polizaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la poliza con id " + id));
    }

    public PolizaResponse crear(PolizaRequest request) {
        Poliza poliza = polizaMapper.aEntidad(request);
        return polizaMapper.aResponse(polizaRepository.save(poliza));
    }

    public PolizaResponse actualizar(Integer id, PolizaRequest request) {
        Poliza poliza = buscar(id);
        polizaMapper.aplicar(poliza, request);
        return polizaMapper.aResponse(polizaRepository.save(poliza));
    }

    public void eliminar(Integer id) {
        polizaRepository.delete(buscar(id));
    }
}
