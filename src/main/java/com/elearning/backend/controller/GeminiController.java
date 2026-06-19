package com.elearning.backend.controller;

import com.elearning.backend.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/chat")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public Map<String, String> chat(
            @RequestBody List<Map<String, String>> messages
    ) {

        String response = geminiService.askGemini(messages);

        return Map.of(
                "reply",
                response
        );
    }
}