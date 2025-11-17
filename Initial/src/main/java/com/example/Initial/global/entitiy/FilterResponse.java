package com.example.Initial.global.entitiy;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class FilterResponse {

    private void sendJsonResponse(HttpServletResponse response, int status, String statusMessage, String message, Optional<String> actionRequired)
            throws IOException {
        log.info("Sending JSON response - Status: {}, Message: {}", statusMessage, message);
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String actionRequiredJson = actionRequired.map(action -> ", \"actionRequired\": \"" + action + "\"").orElse("");
        String body = String.format("{\n" +
                "  \"status\": \"%s\",\n" +
                "  \"message\": \"%s\"%s\n" +
                "}", statusMessage, message, actionRequiredJson);
        response.getWriter().write(body);
    }
}
