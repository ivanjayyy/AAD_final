package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.ArtistDTO;

import java.util.List;

public interface ArtistService {
    void addArtist(ArtistDTO artistDTO);
    List<ArtistDTO> getAllArtists();
    void deleteArtist(Long id);
    void updateArtist(ArtistDTO artistDTO);
    ArtistDTO findArtist(Long id);
}
