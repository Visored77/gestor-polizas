package com.gestor.polizas.mapper;

import com.gestor.polizas.dto.RiesgoRequest;
import com.gestor.polizas.dto.RiesgoResponse;
import com.gestor.polizas.entity.enums.EstadoRiesgo;
import com.gestor.polizas.entity.Riesgo;
import org.springframework.stereotype.Component;

@Component
public class RiesgoMapper {

    public Riesgo aEntidad(RiesgoRequest request) {
        Riesgo riesgo = new Riesgo();
        aplicar(riesgo, request);
        return riesgo;
    }

    private void aplicar(Riesgo riesgo, RiesgoRequest request) {
        riesgo.setInmuebleDireccion(request.inmuebleDireccion());
        riesgo.setArrendatarioNombre(request.arrendatarioNombre());
        riesgo.setArrendatarioDocumento(request.arrendatarioDocumento());
        riesgo.setCanonIndividual(request.canonIndividual());
        if (request.estado() != null) {
            riesgo.setEstado(request.estado());
        } else if (riesgo.getEstado() == null) {
            riesgo.setEstado(EstadoRiesgo.ACTIVO);
        }
    }

    public RiesgoResponse aResponse(Riesgo riesgo) {
        Integer polizaId = riesgo.getPoliza() != null ? riesgo.getPoliza().getId() : null;
        return new RiesgoResponse(
                riesgo.getId(),
                polizaId,
                riesgo.getInmuebleDireccion(),
                riesgo.getArrendatarioNombre(),
                riesgo.getArrendatarioDocumento(),
                riesgo.getCanonIndividual(),
                riesgo.getEstado(),
                riesgo.getCreatedAt(),
                riesgo.getUpdatedAt()
        );
    }
}
