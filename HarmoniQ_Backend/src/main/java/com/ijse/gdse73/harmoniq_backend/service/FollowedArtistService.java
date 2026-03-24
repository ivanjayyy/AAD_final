package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.FollowedArtistDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Music;

import java.util.List;

public interface FollowedArtistService {
    String addFollowedArtist(FollowedArtistDTO followedArtistDTO);

    String checkFollowStatus(FollowedArtistDTO followedArtistDTO);

    List<Music> getRandomFollowedArtistMusic(Long userId, int artistCount, int songsPerArtist);
}
