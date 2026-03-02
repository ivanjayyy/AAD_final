package com.ijse.gdse73.harmoniq_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusicDTO {
    private Long id;
    private String fileName;
    private String musicPath;
    private String thumbnailPath;
    private String musicTitle;
    private String musicArtist;
}
