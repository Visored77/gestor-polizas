package com.gestor.polizas.mapper;

import com.gestor.polizas.dto.PolizaRequest;
import com.gestor.polizas.dto.PolizaResponse;
import com.gestor.polizas.entity.EstadoPoliza;
import com.gestor.polizas.entity.Poliza;
import org.springframework.stereotype.Component;

@Component
public class PolizaMapper {

    /** Crea una nueva entidad a partir del request. */
    public Poliza aEntidad(PolizaRequest request) {
        Poliza poliza = new Poliza();
        aplicar(poliza, request);
        return poliza;
    }

    /** Aplica los datos del request sobre una entidad existente (crear/actualizar). */
    public void aplicar(Poliza poliza, PolizaRequest request) {
        poliza.setTipoPoliza(request.tipoPoliza());
        poliza.setTomadorNombre(request.tomadorNombre());
        poliza.setTomadorDocumento(request.tomadorDocumento());
        poliza.setAseguradoNombre(request.aseguradoNombre());
        poliza.setAseguradoDocumento(request.aseguradoDocumento());
        poliza.setBeneficiarioNombre(request.beneficiarioNombre());
        poliza.setBeneficiarioDocumento(request.beneficiarioDocumento());
        poliza.setVigenciaInicio(request.vigenciaInicio());
        poliza.setVigenciaFin(request.vigenciaFin());
        poliza.setCanonMensual(request.canonMensual());
        poliza.setMesesVigencia(request.mesesVigencia());
        // Regla de negocio: la prima se calcula automaticamente (canon mensual * meses de vigencia)
        poliza.setValorPrima(calcularPrima(request.canonMensual(), request.mesesVigencia()));
        if (request.estado() != null) {
            poliza.setEstado(request.estado());
        } else if (poliza.getEstado() == null) {
            poliza.setEstado(EstadoPoliza.ACTIVA);
        }
    }

    /** Calcula la prima: canon mensual multiplicado por el numero de meses de vigencia. */
    public java.math.BigDecimal calcularPrima(java.math.BigDecimal canonMensual, Integer mesesVigencia) {
        if (canonMensual == null || mesesVigencia == null) {
            return null;
        }
        return canonMensual.multiply(java.math.BigDecimal.valueOf(mesesVigencia));
    }

    public PolizaResponse aResponse(Poliza poliza) {
        return new PolizaResponse(
                poliza.getId(),
                poliza.getTipoPoliza(),
                poliza.getTomadorNombre(),
                poliza.getTomadorDocumento(),
                poliza.getAseguradoNombre(),
                poliza.getAseguradoDocumento(),
                poliza.getBeneficiarioNombre(),
                poliza.getBeneficiarioDocumento(),
                poliza.getVigenciaInicio(),
                poliza.getVigenciaFin(),
                poliza.getCanonMensual(),
                poliza.getMesesVigencia(),
                poliza.getValorPrima(),
                poliza.getEstado(),
                poliza.getCreatedAt(),
                poliza.getUpdatedAt()
        );
    }
}
