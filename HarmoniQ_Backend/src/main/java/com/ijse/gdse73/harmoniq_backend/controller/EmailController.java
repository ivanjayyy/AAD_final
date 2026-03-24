package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public String sendEmail() {
        emailService.sendEmail(
                "phoenixf0209@gmail.com",
                "Test Email",
                "Hello from Spring Boot 🚀"
        );
        return "Email Sent!";
    }
}
