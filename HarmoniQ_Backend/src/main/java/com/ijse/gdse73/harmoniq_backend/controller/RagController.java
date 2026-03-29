//package com.ijse.gdse73.harmoniq_backend.controller;
//
//import com.ijse.gdse73.harmoniq_backend.service.ai.RagService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/rag")
//@RequiredArgsConstructor
//@CrossOrigin
//public class RagController {
//
//    private final RagService ragService;
//
//    @PostMapping
//    public Map<String, String> chat(@RequestBody Map<String, String> req) throws Exception {
//        String reply = ragService.chat(req.get("message"));
//        return Map.of("reply", reply);
//    }
//}
