package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.EmailDataDTO;
import com.ijse.gdse73.harmoniq_backend.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/email")
@CrossOrigin
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public String sendEmail(@RequestBody EmailDataDTO emailData) {
        emailService.sendEmail(emailData.getTo(),emailData.getSubject(),emailData.getBody());
        return "Email Sent!";
    }
}
