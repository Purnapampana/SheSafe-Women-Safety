
package com.womensecurity.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class BrevoEmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name:SheSafe Security Team}")
    private String senderName;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.brevo.com/v3")
            .build();

    public void sendEmail(
            String recipientEmail,
            String recipientName,
            String subject,
            String content) {

        Map<String, Object> body = Map.of(
                "sender", Map.of(
                        "name", senderName,
                        "email", senderEmail
                ),
                "to", List.of(Map.of(
                        "email", recipientEmail,
                        "name", recipientName == null ? "" : recipientName
                )),
                "subject", subject,
                "textContent", content
        );

        restClient.post()
                .uri("/smtp/email")
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    public void sendEmail(
            String recipientEmail,
            String subject,
            String content) {

        sendEmail(recipientEmail, "", subject, content);
    }
}
