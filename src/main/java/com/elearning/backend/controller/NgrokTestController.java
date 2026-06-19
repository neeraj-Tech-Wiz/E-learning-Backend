package com.elearning.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NgrokTestController {
    @GetMapping("/ping")
    public String ping() {
        return "Backend is live ✅";
    }
}
