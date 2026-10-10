package com.gestor.polizas.mapper;

import com.gestor.polizas.dto.AuditoriaCoreRequest;
import com.gestor.polizas.dto.AuditoriaCoreResponse;
import com.gestor.polizas.entity.AuditoriaCore;
import com.gestor.polizas.entity.EstadoSincronizacion;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaCoreMapper {

    public AuditoriaCore aEntidad(AuditoriaCoreRequest request) {
        AuditoriaCore auditoria = new AuditoriaCore();
        aplicar(auditoria, request);
        return auditoria;
    }

    /**
     * Aplica los campos simples del request. La asociacion con Poliza
     * se resuelve en el servicio a partir de request.polizaId().
     */
    public void aplicar(AuditoriaCore auditoria, AuditoriaCoreRequest request) {
        auditoria.setAccion(request.accion());
        auditoria.setPayloadEnviado(request.payloadEnviado());
        if (request.estadoSincronizacion() != null) {
            auditoria.setEstadoSincronizacion(request.estadoSincronizacion());
        } else if (auditoria.getEstadoSincronizacion() == null) {
            auditoria.setEstadoSincronizacion(EstadoSincronizacion.PENDIENTE);
        }
        if (request.intentos() != null) {
            auditoria.setIntentos(request.intentos());
        }
        auditoria.setRespuestaCore(request.respuestaCore());
    }

    public AuditoriaCoreResponse aResponse(AuditoriaCore auditoria) {
        Integer polizaId = auditoria.getPoliza() != null ? auditoria.getPoliza().getId() : null;
        return new AuditoriaCoreResponse(
                auditoria.getId(),
                polizaId,
                auditoria.getAccion(),
                auditoria.getPayloadEnviado(),
                auditoria.getEstadoSincronizacion(),
                auditoria.getIntentos(),
                auditoria.getRespuestaCore(),
                auditoria.getCreatedAt(),
                auditoria.getUpdatedAt()
        );
    }
}
