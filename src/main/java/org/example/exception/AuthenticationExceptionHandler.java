package org.example.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * Handles authentication and authorization errors.
 * Returns consistent JSON error responses using ApiResponse format.
 */
@Component
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationExceptionHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        logger.warn("Authentication failed: {}", authException.getMessage());
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ApiResponse<Object> apiResponse = ApiResponse.error(
                "UNAUTHORIZED",
                "Authentication required. Please provide a valid JWT token.",
                null
        );
        apiResponse.setTimestamp(Instant.now());
        
        try {
            objectMapper.writeValue(response.getWriter(), apiResponse);
        } catch (Exception e) {
            logger.error("Failed to write error response", e);
        }
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        logger.warn("Access denied: {}", accessDeniedException.getMessage());
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ApiResponse<Object> apiResponse = ApiResponse.error(
                "FORBIDDEN",
                "Access denied. You do not have permission to access this resource.",
                null
        );
        apiResponse.setTimestamp(Instant.now());
        
        try {
            objectMapper.writeValue(response.getWriter(), apiResponse);
        } catch (Exception e) {
            logger.error("Failed to write error response", e);
        }
    }
}

