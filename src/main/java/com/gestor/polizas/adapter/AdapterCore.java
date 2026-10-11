package com.gestor.polizas.adapter;

import com.gestor.polizas.dto.EventoCoreRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Adaptador unico de integracion con el CORE de seguros.
 *
 * Centraliza toda la comunicacion con el CORE: polizas y riesgos lo usan
 * para notificar los cambios de estado (creacion, renovacion, cancelacion, etc.).
 *
 * Actualmente invoca por HTTP el mock del CORE
 * (POST {core.mock.base-url}/core-mock/evento) con el header x-api-key.
 * Si en el futuro el CORE pasa a ser el WebLogic real, basta con cambiar
 * la URL (core.mock.base-url) o la implementacion de este adaptador, sin
 * tocar PolizaService ni RiesgoService.
 *
 * Es resiliente: si la llamada falla, registra el error pero NO interrumpe
 * la operacion de negocio.
 */
@Component
public class AdapterCore {

    private static final Logger log = LoggerFactory.getLogger(AdapterCore.class);

    private final RestClient coreRestClient;
    private final String apiKey;

    public AdapterCore(RestClient coreRestClient, @Value("${api.key}") String apiKey) {
        this.coreRestClient = coreRestClient;
        this.apiKey = apiKey;
    }

    /**
     * Notifica un evento al CORE.
     *
     * @param evento   tipo de evento (p. ej. "CREACION", "RENOVACION", "AGREGAR_RIESGO")
     * @param polizaId id de la poliza afectada
     */
    public void notificarEvento(String evento, Integer polizaId) {
        EventoCoreRequest body = new EventoCoreRequest(evento, polizaId);
        try {
            coreRestClient.post()
                    .uri("/core-mock/evento")
                    .header("x-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Evento '{}' notificado al CORE para polizaId={}", evento, polizaId);
        } catch (RuntimeException ex) {
            log.error("No se pudo notificar el evento '{}' al CORE para polizaId={}: {}",
                    evento, polizaId, ex.getMessage());
        }
    }
}
