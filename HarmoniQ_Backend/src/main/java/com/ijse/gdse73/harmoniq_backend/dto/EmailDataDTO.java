package com.ijse.gdse73.harmoniq_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDataDTO {
    private String to;
    private String subject;
    private String body;
}
