package com.gestor.polizas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Seguridad minima: exige el header 'x-api-key' con la clave configurada
 * unicamente en el endpoint del mock del CORE (/core-mock/**).
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER = "x-api-key";
    private static final String RUTA_PROTEGIDA = "/core-mock";

    @Value("${api.key:123456}")
    private String apiKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Solo se aplica a /core-mock; el resto de la API queda sin este filtro.
        return !request.getRequestURI().startsWith(RUTA_PROTEGIDA);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String provided = request.getHeader(HEADER);

        if (apiKey.equals(provided)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                "{\"estado\":401,\"error\":\"No autorizado\","
                        + "\"mensaje\":\"Falta o es invalido el header x-api-key\"}");
    }
}
