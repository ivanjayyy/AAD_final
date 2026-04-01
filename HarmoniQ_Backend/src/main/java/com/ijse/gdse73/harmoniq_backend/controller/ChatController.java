package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.service.ai.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/response")
    public String chat(@RequestBody Map<String, String> req) {
        return chatService.askAI(req.get("message"));
    }

    @PostMapping("/start")
    public void startAI(@RequestBody Map<String, Long> req) {
        chatService.setContext(req.get("userId"));
    }
}
