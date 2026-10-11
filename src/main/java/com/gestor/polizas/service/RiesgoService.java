package com.gestor.polizas.service;

import com.gestor.polizas.core.AccionCore;
import com.gestor.polizas.dto.RiesgoRequest;
import com.gestor.polizas.dto.RiesgoResponse;
import com.gestor.polizas.entity.EstadoRiesgo;
import com.gestor.polizas.entity.Poliza;
import com.gestor.polizas.entity.Riesgo;
import com.gestor.polizas.entity.TipoPoliza;
import com.gestor.polizas.exception.RecursoNoEncontradoException;
import com.gestor.polizas.exception.ReglaNegocioException;
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
    private final SincronizacionCoreService sincronizacionCore;

    public RiesgoService(RiesgoRepository riesgoRepository,
                         RiesgoMapper riesgoMapper,
                         PolizaService polizaService,
                         SincronizacionCoreService sincronizacionCore) {
        this.riesgoRepository = riesgoRepository;
        this.riesgoMapper = riesgoMapper;
        this.polizaService = polizaService;
        this.sincronizacionCore = sincronizacionCore;
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

    /**
     * Agrega un riesgo a una poliza. Segun el alcance esencial, solo las polizas
     * COLECTIVAS admiten agregar riesgos por este endpoint. Las individuales
     * tienen su unico riesgo definido al crearse.
     */
    public RiesgoResponse crear(Integer polizaId, RiesgoRequest request) {
        Poliza poliza = polizaService.buscar(polizaId);

        if (poliza.getTipoPoliza() != TipoPoliza.COLECTIVA) {
            throw new ReglaNegocioException(
                    "Solo las polizas colectivas pueden agregar riesgos. Una poliza individual tiene un unico riesgo");
        }

        Riesgo riesgo = riesgoMapper.aEntidad(request);
        riesgo.setPoliza(poliza);
        riesgo.setEstado(EstadoRiesgo.ACTIVO);
        Riesgo guardado = riesgoRepository.save(riesgo);
        sincronizacionCore.sincronizar(AccionCore.AGREGAR_RIESGO, poliza, riesgoMapper.aResponse(guardado));
        return riesgoMapper.aResponse(guardado);
    }

    public RiesgoResponse actualizar(Integer id, RiesgoRequest request) {
        Riesgo riesgo = buscar(id);
        riesgoMapper.aplicar(riesgo, request);
        Riesgo guardado = riesgoRepository.save(riesgo);
        sincronizacionCore.sincronizar(AccionCore.AGREGAR_RIESGO, riesgo.getPoliza(), riesgoMapper.aResponse(guardado));
        return riesgoMapper.aResponse(guardado);
    }

    /**
     * Cancela un riesgo (cambio de estado a CANCELADO). Aplica a polizas colectivas,
     * que pueden amparar varios inmuebles. En individuales no se permite cancelar
     * el unico riesgo (equivaldria a cancelar la poliza).
     */
    public RiesgoResponse cancelar(Integer id) {
        Riesgo riesgo = buscar(id);

        if (riesgo.getEstado() == EstadoRiesgo.CANCELADO) {
            throw new ReglaNegocioException("El riesgo ya se encuentra cancelado");
        }
        if (riesgo.getPoliza().getTipoPoliza() == TipoPoliza.INDIVIDUAL) {
            throw new ReglaNegocioException(
                    "No se puede cancelar el unico riesgo de una poliza individual; cancele la poliza en su lugar");
        }

        riesgo.setEstado(EstadoRiesgo.CANCELADO);
        Riesgo guardado = riesgoRepository.save(riesgo);
        sincronizacionCore.sincronizar(AccionCore.CANCELAR_RIESGO, riesgo.getPoliza(), riesgoMapper.aResponse(guardado));
        return riesgoMapper.aResponse(guardado);
    }

    public void eliminar(Integer id) {
        riesgoRepository.delete(buscar(id));
    }
}
