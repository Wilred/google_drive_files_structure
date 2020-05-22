package com.tarasenko.google_drive_files_structure.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @GetMapping("/greeting")
    public String greeting() {
        return "<h1>Welcome to Secured Site</h1>";
    }
}
