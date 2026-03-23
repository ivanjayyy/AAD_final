package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.FollowedArtistDTO;

public interface FollowedArtistService {
    String addFollowedArtist(FollowedArtistDTO followedArtistDTO);

    String checkFollowStatus(FollowedArtistDTO followedArtistDTO);
}
