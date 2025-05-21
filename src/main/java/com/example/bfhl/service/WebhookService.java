package com.example.bfhl.service;

import com.example.bfhl.model.WebhookResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WebhookService {

    @Value("${webhook.url}")
    private String webhookUrl;

    @Value("${submit.url}")
    private String submitUrl;

    @Value("${name}")
    private String name;

    @Value("${regNo}")
    private String regNo;

    @Value("${email}")
    private String email;

    public void handleStartupProcess() {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> request = Map.of(
                "name", name,
                "regNo", regNo,
                "email", email
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                webhookUrl, entity, WebhookResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            String webhook = response.getBody().getWebhook();
            String token = response.getBody().getAccessToken();

            String finalQuery = getSqlQuery(regNo);

            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setBearerAuth(token);
            authHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> finalEntity = new HttpEntity<>(
                    Map.of("finalQuery", finalQuery),
                    authHeaders
            );

            ResponseEntity<String> finalResponse = restTemplate.postForEntity(
                    webhook, finalEntity, String.class
            );

            System.out.println("Submitted. Server responded: " + finalResponse.getBody());
        } else {
            System.out.println("Webhook generation failed.");
        }
    }

    private String getSqlQuery(String regNo) {
        int lastDigit = Integer.parseInt(regNo.replaceAll("\\D+", "")) % 10;
        boolean isEven = lastDigit % 2 == 0;

        if (isEven) {
            return "SELECT * FROM students WHERE marks > 75;"; 
        } else {
            return "SELECT name FROM employees WHERE salary > 50000;"; 
        }
    }
}

