package fr.arthur.devoir_java.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Retourne une erreur 401 Unauthorized quand quelqu'un essaie d'accéder
        // à une ressource protégée sans authentification appropriée
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}