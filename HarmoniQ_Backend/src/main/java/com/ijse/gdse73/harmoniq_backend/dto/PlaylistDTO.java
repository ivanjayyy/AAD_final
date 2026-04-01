package com.ijse.gdse73.harmoniq_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistDTO {
    private Long id;
    @NotBlank(message = "Playlist name cannot be empty")
    private String playlistName;
    private Long userId;
}
