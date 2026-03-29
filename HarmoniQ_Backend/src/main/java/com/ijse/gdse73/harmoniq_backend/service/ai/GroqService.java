package com.ijse.gdse73.harmoniq_backend.service.ai;

import com.ijse.gdse73.harmoniq_backend.dto.ChatRequestDTO;
import com.ijse.gdse73.harmoniq_backend.dto.ChatResponseDTO;
import com.ijse.gdse73.harmoniq_backend.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroqService {

    private final WebClient webClient;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String model;

    public String chat(String userMessage) {

        ChatRequestDTO request = new ChatRequestDTO(
                model,
                List.of(new MessageDTO("user", userMessage))
        );

        ChatResponseDTO response = webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponseDTO.class)
                .block();

        return response.getChoices().get(0).getMessage().getContent();
    }
}
