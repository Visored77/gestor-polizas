package com.gestor.polizas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GestorPolizasApplicationTests {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper json = new ObjectMapper();

    private static final String KEY = "x-api-key";
    private static final String VAL = "123456";

    private static final String COLECTIVA = """
            {"tipoPoliza":"COLECTIVA","tomadorNombre":"Inmob","tomadorDocumento":"NIT1",
             "aseguradoNombre":"Arr","aseguradoDocumento":"NIT2","beneficiarioNombre":"Ben",
             "beneficiarioDocumento":"NIT3","vigenciaInicio":"2026-01-01","vigenciaFin":"2027-01-01",
             "canonMensual":2000000.00,"mesesVigencia":12}""";

    private static final String INDIVIDUAL = """
            {"tipoPoliza":"INDIVIDUAL","tomadorNombre":"Pedro","tomadorDocumento":"CC1",
             "aseguradoNombre":"Pedro","aseguradoDocumento":"CC1","beneficiarioNombre":"Arr",
             "beneficiarioDocumento":"CC2","vigenciaInicio":"2026-01-01","vigenciaFin":"2027-01-01",
             "canonMensual":1000000.00,"mesesVigencia":12}""";

    private static final String RIESGO = """
            {"inmuebleDireccion":"Torre A","arrendatarioNombre":"A","arrendatarioDocumento":"CCA",
             "canonIndividual":1000000.00}""";

    private int crearPoliza(String body) throws Exception {
        MvcResult r = mvc.perform(post("/polizas").header(KEY, VAL)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return json.readTree(r.getResponse().getContentAsString()).get("id").asInt();
    }

    @Test
    void polizas_noRequiereApiKey() throws Exception {
        // La seguridad por api-key aplica solo al mock; /polizas queda libre.
        mvc.perform(get("/polizas")).andExpect(status().isOk());
    }

    @Test
    void mockCore_sinApiKey_devuelve401() throws Exception {
        mvc.perform(post("/core-mock/evento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"evento\":\"ACTUALIZACION\",\"polizaId\":555}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void primaSeCalculaAutomaticamente() throws Exception {
        MvcResult r = mvc.perform(post("/polizas").header(KEY, VAL)
                        .contentType(MediaType.APPLICATION_JSON).content(INDIVIDUAL))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorPrima").value(12000000.00))
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andReturn();
        // 1.000.000 * 12 = 12.000.000
        JsonNode n = json.readTree(r.getResponse().getContentAsString());
        assert n.get("valorPrima").asDouble() == 12000000.00;
    }

    @Test
    void agregarRiesgo_soloColectiva() throws Exception {
        int colectiva = crearPoliza(COLECTIVA);
        mvc.perform(post("/polizas/" + colectiva + "/riesgos").header(KEY, VAL)
                        .contentType(MediaType.APPLICATION_JSON).content(RIESGO))
                .andExpect(status().isCreated());

        int individual = crearPoliza(INDIVIDUAL);
        mvc.perform(post("/polizas/" + individual + "/riesgos").header(KEY, VAL)
                        .contentType(MediaType.APPLICATION_JSON).content(RIESGO))
                .andExpect(status().isConflict());
    }

    @Test
    void getRiesgosDePoliza() throws Exception {
        int colectiva = crearPoliza(COLECTIVA);
        mvc.perform(post("/polizas/" + colectiva + "/riesgos").header(KEY, VAL)
                .contentType(MediaType.APPLICATION_JSON).content(RIESGO));
        mvc.perform(get("/polizas/" + colectiva + "/riesgos").header(KEY, VAL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void renovar_incrementaPorIpc_yEstadoRenovada() throws Exception {
        int colectiva = crearPoliza(COLECTIVA);
        mvc.perform(post("/polizas/" + colectiva + "/renovar").header(KEY, VAL)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"ipcPorcentaje\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canonMensual").value(2200000.00))
                .andExpect(jsonPath("$.valorPrima").value(26400000.00))
                .andExpect(jsonPath("$.estado").value("RENOVADA"));
    }

    @Test
    void noSePuedeRenovarPolizaCancelada() throws Exception {
        int colectiva = crearPoliza(COLECTIVA);
        mvc.perform(post("/polizas/" + colectiva + "/cancelar").header(KEY, VAL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
        mvc.perform(post("/polizas/" + colectiva + "/renovar").header(KEY, VAL)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"ipcPorcentaje\":5}"))
                .andExpect(status().isConflict());
    }

    @Test
    void cancelarPoliza_cancelaRiesgosEnCascada() throws Exception {
        int colectiva = crearPoliza(COLECTIVA);
        mvc.perform(post("/polizas/" + colectiva + "/riesgos").header(KEY, VAL)
                .contentType(MediaType.APPLICATION_JSON).content(RIESGO));
        mvc.perform(post("/polizas/" + colectiva + "/riesgos").header(KEY, VAL)
                .contentType(MediaType.APPLICATION_JSON).content(RIESGO));

        mvc.perform(post("/polizas/" + colectiva + "/cancelar").header(KEY, VAL))
                .andExpect(status().isOk());

        mvc.perform(get("/polizas/" + colectiva + "/riesgos").header(KEY, VAL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("CANCELADO"))
                .andExpect(jsonPath("$[1].estado").value("CANCELADO"));
    }

    @Test
    void filtroPorTipoYEstado() throws Exception {
        crearPoliza(COLECTIVA);
        crearPoliza(INDIVIDUAL);
        mvc.perform(get("/polizas?tipo=COLECTIVA").header(KEY, VAL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoPoliza").value("COLECTIVA"));
        mvc.perform(get("/polizas?estado=ACTIVA").header(KEY, VAL))
                .andExpect(status().isOk());
    }

    @Test
    void mockCore_registraEvento() throws Exception {
        mvc.perform(post("/core-mock/evento").header(KEY, VAL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"evento\":\"ACTUALIZACION\",\"polizaId\":555}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recibido").value(true))
                .andExpect(jsonPath("$.polizaId").value(555));
    }
}
