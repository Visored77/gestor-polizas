package com.gestor.polizas.service;

import com.gestor.polizas.adapter.AdapterCore;
import com.gestor.polizas.dto.RiesgoRequest;
import com.gestor.polizas.dto.RiesgoResponse;
import com.gestor.polizas.entity.Poliza;
import com.gestor.polizas.entity.Riesgo;
import com.gestor.polizas.entity.enums.EstadoRiesgo;
import com.gestor.polizas.entity.enums.TipoPoliza;
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
    private final AdapterCore adapterCore;

    public RiesgoService(RiesgoRepository riesgoRepository,
                         RiesgoMapper riesgoMapper,
                         PolizaService polizaService,
                         AdapterCore adapterCore) {
        this.riesgoRepository = riesgoRepository;
        this.riesgoMapper = riesgoMapper;
        this.polizaService = polizaService;
        this.adapterCore = adapterCore;
    }

    @Transactional(readOnly = true)
    public List<RiesgoResponse> listarPorPoliza(Integer polizaId) {
        return riesgoRepository.findByPolizaId(polizaId).stream()
                .map(riesgoMapper::aResponse)
                .toList();
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
        adapterCore.notificarEvento("AGREGAR_RIESGO", poliza.getId());
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
        adapterCore.notificarEvento("CANCELAR_RIESGO", riesgo.getPoliza().getId());
        return riesgoMapper.aResponse(guardado);
    }
}
