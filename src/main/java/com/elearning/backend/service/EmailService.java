package com.elearning.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendWarningEmail(String to, String studentName,
                                 int percentage, int month, int year) {

        String body = String.format(
                "Dear %s,\n\n" +
                        "Your attendance for %d/%d has fallen below the required threshold.\n" +
                        "Current attendance: %d%%.\n\n" +
                        "Please improve your attendance to avoid further action.\n\n" +
                        "Regards,\nSchool Administration",
                studentName, month, year, percentage
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        Map<String, Object> payload = Map.of(
                "sender", Map.of(
                        "name", "eLearning Education",
                        "email", "elearningplatformedu@gmail.com"
                ),
                "to", List.of(
                        Map.of("email", to)
                ),
                "subject", "⚠️ Attendance Warning",
                "textContent", body
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.brevo.com/v3/smtp/email",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
        }  catch (HttpStatusCodeException e) {
            System.out.println("Brevo Error Status: " + e.getStatusCode());
            System.out.println("Brevo Error Body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}