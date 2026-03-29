package com.ijse.gdse73.harmoniq_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatResponseDTO {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private MessageDTO message;
    }
}
