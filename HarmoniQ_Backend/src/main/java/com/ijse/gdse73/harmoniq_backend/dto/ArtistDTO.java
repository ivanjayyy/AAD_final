package com.ijse.gdse73.harmoniq_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistDTO {
    private Long id;
    @NotBlank(message = "Artist name cannot be empty")
    private String name;
    @NotBlank(message = "Artist bio cannot be empty")
    private String bio;
    @NotBlank(message = "Artist profile pic cannot be empty")
    private String pfpPath;
}
