package filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

@Component
public class JwtValidationFilter extends OncePerRequestFilter {
    private static final List<String> VALID_SUBJECTS = Arrays.asList("starlord", "gamora", "drax", "rocket", "groot");
    private static final String VALID_ISSUER = "cmu.edu";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = Logger.getLogger(JwtValidationFilter.class.getName());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("Incoming request to: " + request.getRequestURI());

        // Skip validation for /status endpoint
        if (request.getRequestURI().equals("/status")) {
            logger.info("Skipping validation for /status endpoint.");
            filterChain.doFilter(request, response);
            return;
        }

        // Check Client Type header
        String clientType = request.getHeader("X-Client-Type");
        logger.info("X-Client-Type header: " + clientType);
        if (clientType == null) {
            logger.warning("Missing required header: X-Client-Type");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Missing required header: X-Client-Type\"}");
            return;
        }

        if (!clientType.equals("web")) {
            logger.warning("Invalid X-Client-Type header");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid X-Client-Type header\"}");
            return;
        }

        // Check Authorization header
        String authHeader = request.getHeader("Authorization");
        logger.info("Authorization header: " + authHeader);
        if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer ")) {
            logger.warning("Missing or invalid Authorization header");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Missing or invalid Authorization header\"}");
            return;
        }

        // Validate JWT token
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        try {
            logger.info("Token: " + token);
            validateJwtToken(token);
        } catch (Exception e) {
            logger.severe("JWT validation failed: " + e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            return;
        }

        logger.info("JWT validation successful. Proceeding with request.");
        filterChain.doFilter(request, response);
    }

    private void validateJwtToken(String token) throws Exception {
        try {
            // Decode JWT token
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new Exception("Invalid token format");
            }

            // Decode payload (second part)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            logger.info("Decoded JWT Payload: " + payload);
            JsonNode payloadJson = objectMapper.readTree(payload);

            // Validate token claims
            if (!payloadJson.has("sub") || !VALID_SUBJECTS.contains(payloadJson.get("sub").asText())) {
                throw new Exception("Invalid subject in token");
            }

            if (!payloadJson.has("iss") || !VALID_ISSUER.equals(payloadJson.get("iss").asText())) {
                throw new Exception("Invalid issuer in token");
            }

        } catch (Exception e) {
            throw new Exception("Invalid JWT token: " + e.getMessage());
        }
    }
}
