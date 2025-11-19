package com.digital.google_meet_config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GoogleTokenService {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.token.url}")
    private String tokenUrl;

    private final WebClient webClient;

    public GoogleTokenService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public String getAccessTokenFromRefreshToken(String refreshToken) {
        String body = "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&refresh_token=" + refreshToken +
                "&grant_type=refresh_token";

        Map<String, Object> response = webClient.post()
                .uri(tokenUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (response != null && response.get("access_token") != null) {
            return response.get("access_token").toString();
        }

        throw new RuntimeException("Failed to get access token from refresh token");
    }
}

