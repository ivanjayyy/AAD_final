package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.ArtistDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Artist;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.repo.ArtistRepo;
import com.ijse.gdse73.harmoniq_backend.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    private final ArtistRepo artistRepo;
    private final ModelMapper modelMapper;

    @Override
    public void addArtist(ArtistDTO artistDTO) {
        if (artistRepo.findByName(artistDTO.getName()).isPresent()) {
            return;
        }
        artistRepo.save(modelMapper.map(artistDTO, Artist.class));
    }

    @Override
    public List<ArtistDTO> getAllArtists() {
        return artistRepo.findAll()
                .stream()
                .map(artist -> modelMapper.map(artist, ArtistDTO.class))
                .toList();
    }

    @Override
    public void deleteArtist(Long id) {
        artistRepo.findById(id).orElseThrow(() -> new CustomException("Artist not found"));
        artistRepo.deleteById(id);
    }

    @Override
    public void updateArtist(ArtistDTO artistDTO) {
        Artist artist = artistRepo.findById(artistDTO.getId()).orElseThrow(() -> new CustomException("Artist not found"));
        artist.setName(artistDTO.getName());
        artist.setBio(artistDTO.getBio());
        artistRepo.save(artist);
    }

    @Override
    public ArtistDTO findArtist(Long id) {
        return artistRepo.findById(id).map(artist -> modelMapper.map(artist, ArtistDTO.class)).orElseThrow(() -> new CustomException("Artist not found"));
    }
}
