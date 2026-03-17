package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.LikedOrRecentSongDTO;
import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;

import java.util.List;

public interface RecentSongService {
    void addRecentSong(LikedOrRecentSongDTO likedOrRecentSongDTO);
    List<MusicDTO> loadRecentSongs(Long id);
}
