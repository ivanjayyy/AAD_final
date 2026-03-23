package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.LikedOrRecentSongDTO;
import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;

import java.util.List;

public interface LikedSongService {
    String addOrRemoveLike(LikedOrRecentSongDTO likedOrRecentSongDTO);
    List<MusicDTO> getLikedSongsByUser(Long id);

    String checkLikeStatus(LikedOrRecentSongDTO likedOrRecentSongDTO);
}
