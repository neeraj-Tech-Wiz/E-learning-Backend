package com.elearning.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public String askGemini(List<Map<String, String>> history) {

        try {

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

            // Build the JSON body using Jackson nodes for safety
            ObjectNode rootNode = mapper.createObjectNode();
            ArrayNode contentsArray = rootNode.putArray("contents");

            for (Map<String, String> message : history) {
                ObjectNode contentNode = contentsArray.addObject();
                // Gemini expects roles: "user" and "model"
                contentNode.put("role", message.get("role"));

                ArrayNode partsArray = contentNode.putArray("parts");
                partsArray.addObject().put("text", message.get("content"));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity =
                    new HttpEntity<>(rootNode.toString(), headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            url,
                            entity,
                            String.class
                    );

            JsonNode json =
                    mapper.readTree(response.getBody());

            return json
                    .get("candidates")
                    .get(0)
                    .get("content")
                    .get("parts")
                    .get(0)
                    .get("text")
                    .asText();

        } catch (Exception e) {

            e.printStackTrace();

            return "Gemini AI is temporarily unavailable. Please try again later.";
        }
    }
}