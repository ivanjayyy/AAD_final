package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.LikedOrRecentSongDTO;

import java.util.List;

public interface LikedSongService {
    String addOrRemoveLike(LikedOrRecentSongDTO likedOrRecentSongDTO);
    List<LikedOrRecentSongDTO> getLikedSongsByUser(Long id);
}
