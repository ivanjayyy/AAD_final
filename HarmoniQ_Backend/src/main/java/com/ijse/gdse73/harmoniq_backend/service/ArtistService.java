package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.ArtistDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Artist;

import java.util.List;

public interface ArtistService {
    void addArtist(ArtistDTO artistDTO);
    List<ArtistDTO> getAllArtists();
    Artist deleteArtist(Long id);
    void updateArtist(ArtistDTO artistDTO);
    ArtistDTO findArtist(Long id);
}
