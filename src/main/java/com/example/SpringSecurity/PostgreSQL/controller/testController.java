package com.example.SpringSecurity.PostgreSQL.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class testController {

    @GetMapping("/testSecurity")
    public String hello() {
        return  "Testing Security";
    }
}
