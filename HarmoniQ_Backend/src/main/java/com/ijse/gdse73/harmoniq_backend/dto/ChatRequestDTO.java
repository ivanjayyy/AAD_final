package com.ijse.gdse73.harmoniq_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDTO {
    private String model;
    private List<MessageDTO> messages;
}
