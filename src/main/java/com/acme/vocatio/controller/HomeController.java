package com.acme.vocatio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "¡Bienvenido a Vocatio API! Visita /swagger-ui/swagger-ui.html para explorar los endpoints.";
    }
}