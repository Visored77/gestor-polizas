package com.gestor.polizas.service;

import com.gestor.polizas.core.AccionCore;
import com.gestor.polizas.dto.PolizaRequest;
import com.gestor.polizas.dto.PolizaResponse;
import com.gestor.polizas.dto.RenovacionRequest;
import com.gestor.polizas.entity.EstadoPoliza;
import com.gestor.polizas.entity.EstadoRiesgo;
import com.gestor.polizas.entity.Poliza;
import com.gestor.polizas.entity.Riesgo;
import com.gestor.polizas.entity.TipoPoliza;
import com.gestor.polizas.exception.RecursoNoEncontradoException;
import com.gestor.polizas.exception.ReglaNegocioException;
import com.gestor.polizas.mapper.PolizaMapper;
import com.gestor.polizas.repository.PolizaRepository;
import com.gestor.polizas.repository.RiesgoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class PolizaService {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final PolizaMapper polizaMapper;
    private final SincronizacionCoreService sincronizacionCore;

    public PolizaService(PolizaRepository polizaRepository,
                         RiesgoRepository riesgoRepository,
                         PolizaMapper polizaMapper,
                         SincronizacionCoreService sincronizacionCore) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
        this.polizaMapper = polizaMapper;
        this.sincronizacionCore = sincronizacionCore;
    }

    @Transactional(readOnly = true)
    public List<PolizaResponse> listar(TipoPoliza tipo, EstadoPoliza estado) {
        return polizaRepository.buscarPorFiltros(tipo, estado).stream()
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
        validarVigencia(request);
        Poliza poliza = polizaMapper.aEntidad(request);
        poliza.setEstado(EstadoPoliza.ACTIVA);
        Poliza guardada = polizaRepository.save(poliza);
        // Sincronizacion con el CORE: creacion cambia el estado del sistema.
        sincronizacionCore.sincronizar(AccionCore.CREACION_POLIZA, guardada, polizaMapper.aResponse(guardada));
        return polizaMapper.aResponse(guardada);
    }

    public PolizaResponse actualizar(Integer id, PolizaRequest request) {
        validarVigencia(request);
        Poliza poliza = buscar(id);
        polizaMapper.aplicar(poliza, request);
        Poliza guardada = polizaRepository.save(poliza);
        sincronizacionCore.sincronizar(AccionCore.MODIFICACION_POLIZA, guardada, polizaMapper.aResponse(guardada));
        return polizaMapper.aResponse(guardada);
    }

    /**
     * Renueva la poliza por el mismo periodo de vigencia inicial, ajustando el
     * canon mensual segun el incremento del IPC. Recalcula la prima y desplaza
     * la vigencia. Marca la poliza anterior como RENOVADA.
     */
    public PolizaResponse renovar(Integer id, RenovacionRequest request) {
        Poliza poliza = buscar(id);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException("No se puede renovar una poliza cancelada");
        }

        // Factor de ajuste: canon * (1 + ipc/100)
        BigDecimal factor = BigDecimal.ONE.add(request.ipcPorcentaje().divide(CIEN, 6, RoundingMode.HALF_UP));
        BigDecimal nuevoCanon = poliza.getCanonMensual().multiply(factor).setScale(2, RoundingMode.HALF_UP);

        // Mismo periodo de vigencia inicial (misma cantidad de meses).
        int mesesVigencia = poliza.getMesesVigencia();
        poliza.setCanonMensual(nuevoCanon);
        poliza.setVigenciaInicio(poliza.getVigenciaFin());
        poliza.setVigenciaFin(poliza.getVigenciaFin().plusMonths(mesesVigencia));
        // Prima recalculada con el nuevo canon.
        poliza.setValorPrima(polizaMapper.calcularPrima(nuevoCanon, mesesVigencia));
        poliza.setEstado(EstadoPoliza.RENOVADA);

        Poliza guardada = polizaRepository.save(poliza);
        sincronizacionCore.sincronizar(AccionCore.RENOVACION_POLIZA, guardada, polizaMapper.aResponse(guardada));
        return polizaMapper.aResponse(guardada);
    }

    /** Cancela la poliza y, en cascada, todos sus riesgos activos. Sincroniza con el CORE. */
    public PolizaResponse cancelar(Integer id) {
        Poliza poliza = buscar(id);
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException("La poliza ya se encuentra cancelada");
        }

        // Regla: cancelar la poliza cancela todos sus riesgos.
        List<Riesgo> riesgos = riesgoRepository.findByPolizaId(id);
        for (Riesgo riesgo : riesgos) {
            if (riesgo.getEstado() != EstadoRiesgo.CANCELADO) {
                riesgo.setEstado(EstadoRiesgo.CANCELADO);
            }
        }
        riesgoRepository.saveAll(riesgos);

        poliza.setEstado(EstadoPoliza.CANCELADA);
        Poliza guardada = polizaRepository.save(poliza);
        sincronizacionCore.sincronizar(AccionCore.CANCELACION_POLIZA, guardada, polizaMapper.aResponse(guardada));
        return polizaMapper.aResponse(guardada);
    }

    public void eliminar(Integer id) {
        polizaRepository.delete(buscar(id));
    }

    private void validarVigencia(PolizaRequest request) {
        if (request.vigenciaFin().isBefore(request.vigenciaInicio())
                || request.vigenciaFin().isEqual(request.vigenciaInicio())) {
            throw new ReglaNegocioException("La fecha de fin de vigencia debe ser posterior a la de inicio");
        }
        long meses = ChronoUnit.MONTHS.between(request.vigenciaInicio(), request.vigenciaFin());
        if (meses <= 0) {
            throw new ReglaNegocioException("El periodo de vigencia debe cubrir al menos un mes");
        }
    }
}
