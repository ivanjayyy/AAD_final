package com.ijse.gdse73.harmoniq_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikedOrRecentSongDTO {
    private Long id;
    private Long userId;
    private Long musicId;
}
