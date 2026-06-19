package com.elearning.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @RequestMapping(value = {
            "/",
            "/login",
            "/demo",
            "/teacher/**",
            "/student/**",
            "/admin/**"
    })
    public String forwardReact() {
        return "forward:/index.html";
    }
}
