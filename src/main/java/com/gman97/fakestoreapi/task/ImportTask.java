package com.gman97.fakestoreapi.task;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
@RequiredArgsConstructor
public class ImportTask {

    private final RestTemplate restTemplate;

    @Value("${api.username}")
    private String username;

    @Value("${api.password}")
    private String password;

    @Scheduled(fixedRateString = "PT30M", initialDelayString = "PT30M")
    public void executeImport() {

        HttpHeaders headers = new HttpHeaders();
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        restTemplate.exchange("http://localhost:8080/api/v1/import", HttpMethod.POST, entity, Void.class);

        SecurityContextHolder.clearContext();
    }
}
