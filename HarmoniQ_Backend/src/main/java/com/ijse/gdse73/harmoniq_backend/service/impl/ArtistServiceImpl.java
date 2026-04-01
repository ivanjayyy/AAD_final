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
        if (artistDTO == null) {
            throw new CustomException("Artist Datalist is empty");
        }

        if (artistRepo.findByName(artistDTO.getName()) != null) {
            throw new CustomException("Artist already exists");
        }

        if (artistRepo.findByPfpPath(artistDTO.getPfpPath()) != null) {
            throw new CustomException("Artist profile picture already exists");
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
    public Artist deleteArtist(Long id) {
        Artist artist = artistRepo.findById(id).orElseThrow(() -> new CustomException("Artist not found"));
        artistRepo.deleteById(id);
        return artist;
    }

    @Override
    public void updateArtist(ArtistDTO artistDTO) {
        if (artistDTO == null) {
            throw new CustomException("Artist New Datalist is empty");
        }

        Artist artist = artistRepo.findById(artistDTO.getId()).orElseThrow(() -> new CustomException("Artist not found"));

        if (!artistDTO.getName().equals(artist.getName())) {
            if (artistRepo.findByName(artistDTO.getName()) != null) {
                throw new CustomException("Artist name already exists");
            }
        }

        if (!artistDTO.getPfpPath().equals(artist.getPfpPath())) {
            if (artistRepo.findByPfpPath(artistDTO.getPfpPath()) != null) {
                throw new CustomException("Artist profile pic already exists");
            }
        }

        artist.setName(artistDTO.getName());
        artist.setPfpPath(artistDTO.getPfpPath());
        artist.setBio(artistDTO.getBio());

        artistRepo.save(artist);
    }

    @Override
    public ArtistDTO findArtist(Long id) {
        return artistRepo.findById(id).map(artist -> modelMapper.map(artist, ArtistDTO.class)).orElseThrow(() -> new CustomException("Artist not found"));
    }
}
