package com.ijse.gdse73.harmoniq_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreDTO {
    Long id;
    @NotBlank(message = "Genre name cannot be empty")
    String name;
}
