package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Music;

import java.util.List;

public interface MusicService {
    void saveMusic(MusicDTO musicDTO);
    MusicDTO getMusicById(Long id);
    List<MusicDTO> getAllMusic();
    Music deleteMusic(Long id);
    void updateMusic(MusicDTO musicDTO);
}
