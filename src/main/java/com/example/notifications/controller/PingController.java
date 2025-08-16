package com.example.notifications.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        return "not secure";
    }


    @GetMapping("/protectedPing")
    public String protectedPing() {
        return "secured";
    }
}


