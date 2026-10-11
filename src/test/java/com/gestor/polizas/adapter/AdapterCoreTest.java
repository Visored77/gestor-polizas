package com.gestor.polizas.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * Verifica que AdapterCore realiza la llamada HTTP correcta al CORE:
 * POST /core-mock/evento con el header x-api-key y el cuerpo esperado.
 */
class AdapterCoreTest {

    private MockRestServiceServer server;
    private AdapterCore adapterCore;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost:8080");
        server = MockRestServiceServer.bindTo(builder).build();
        adapterCore = new AdapterCore(builder.build(), "123456");
    }

    @Test
    void notificarEvento_hacePostAlCoreConApiKey() {
        server.expect(requestTo("http://localhost:8080/core-mock/evento"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("x-api-key", "123456"))
                .andExpect(jsonPath("$.evento").value("CREACION"))
                .andExpect(jsonPath("$.polizaId").value(42))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"recibido\":true}"));

        adapterCore.notificarEvento("CREACION", 42);

        server.verify();
    }
}
