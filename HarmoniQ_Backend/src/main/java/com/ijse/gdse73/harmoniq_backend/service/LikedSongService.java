package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.LikedSongDTO;

import java.util.List;

public interface LikedSongService {
    String addOrRemoveLike(LikedSongDTO likedSongDTO);
    List<LikedSongDTO> getLikedSongsByUser(Long id);
}
