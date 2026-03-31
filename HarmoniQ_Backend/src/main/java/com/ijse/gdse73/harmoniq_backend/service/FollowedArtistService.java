package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.ArtistDTO;
import com.ijse.gdse73.harmoniq_backend.dto.FollowedArtistDTO;
import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Music;

import java.util.List;

public interface FollowedArtistService {
    String addFollowedArtist(FollowedArtistDTO followedArtistDTO);

    String checkFollowStatus(FollowedArtistDTO followedArtistDTO);

    List<MusicDTO> getRandomFollowedArtistMusic(Long userId, int artistCount, int songsPerArtist);

    List<ArtistDTO> getFamousArtists();

    List<ArtistDTO> getAllFollowingArtists(Long userId);
}
