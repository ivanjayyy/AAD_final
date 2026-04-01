package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Artist;
import com.ijse.gdse73.harmoniq_backend.entity.Genre;
import com.ijse.gdse73.harmoniq_backend.entity.Music;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.repo.ArtistRepo;
import com.ijse.gdse73.harmoniq_backend.repo.GenreRepo;
import com.ijse.gdse73.harmoniq_backend.repo.MusicRepo;
import com.ijse.gdse73.harmoniq_backend.service.MusicService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {
    private final MusicRepo musicRepo;
    private final ArtistRepo artistRepo;
    private final ModelMapper modelMapper;
    private final GenreRepo genreRepo;

    @Override
    public void saveMusic(MusicDTO musicDTO){
        if (musicDTO == null) {
            throw new CustomException("MusicDTO is null");
        }

        Artist artist = artistRepo.findByName(musicDTO.getMusicArtist());
        if (artist == null) {
            throw new CustomException("Artist not found");
        }

        Genre genre = genreRepo.findGenreById(musicDTO.getMusicGenreId());
        if (genre == null) {
            throw new CustomException("Genre not found");
        }

        if (musicRepo.findByMusicTitle(musicDTO.getMusicTitle()) != null) {
            throw new CustomException("Music title already exists");
        }

        if (musicRepo.findByMusicPath(musicDTO.getMusicPath()) != null) {
            throw new CustomException("Song already exists");
        }

        if (musicRepo.findByThumbnailPath(musicDTO.getThumbnailPath()) != null) {
            throw new CustomException("Thumbnail already exists");
        }

        Music music = Music.builder()
                .fileName(musicDTO.getFileName())
                .musicPath(musicDTO.getMusicPath())
                .thumbnailPath(musicDTO.getThumbnailPath())
                .musicTitle(musicDTO.getMusicTitle())
                .artist(artist)
                .genre(genre)
                .build();

        musicRepo.save(music);
    }

    @Override
    public MusicDTO getMusicById(Long id) {
        return musicRepo.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    @Override
    public List<MusicDTO> getAllMusic() {
        return musicRepo.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    private MusicDTO convertToDto(Music music) {

        modelMapper.typeMap(Music.class, MusicDTO.class)
                .addMappings(mapper -> {
                    mapper.skip(MusicDTO::setMusicArtist);
                    mapper.skip(MusicDTO::setMusicGenreId);
                });

        MusicDTO musicDTO = modelMapper.map(music, MusicDTO.class);

        if (music.getArtist() != null) {
            musicDTO.setMusicArtist(music.getArtist().getName());
        }

        if (music.getGenre() != null) {
            musicDTO.setMusicGenreId(music.getGenre().getId());
        }

        return musicDTO;
    }

    @Override
    @Transactional
    public Music deleteMusic(Long id) {
        Music music = musicRepo.findById(id).orElseThrow(() -> new CustomException("Music not found"));
        musicRepo.deleteById(id);
        return music;
    }

    @Override
    public void updateMusic(MusicDTO musicDTO) {
        if (musicDTO == null) {
            throw new CustomException("MusicDTO is null");
        }

        Music music = musicRepo.findById(musicDTO.getId()).orElseThrow(() -> new CustomException("Music not found"));

        Artist artist = artistRepo.findByName(musicDTO.getMusicArtist());
        if (artist == null) {
            throw new CustomException("Artist not found");
        }

        Genre genre = genreRepo.findGenreById(musicDTO.getMusicGenreId());
        if (genre == null) {
            throw new CustomException("Genre not found");
        }

        if (!musicDTO.getMusicTitle().equals(music.getMusicTitle())) {
            if (musicRepo.findByMusicTitle(musicDTO.getMusicTitle()) != null) {
                throw new CustomException("Music title already exists");
            }
        }

        if (!musicDTO.getMusicPath().equals(music.getMusicPath())) {
            if (musicRepo.findByMusicPath(musicDTO.getMusicPath()) != null) {
                throw new CustomException("Song already exists");
            }
        }

        if (!musicDTO.getThumbnailPath().equals(music.getThumbnailPath())) {
            if (musicRepo.findByThumbnailPath(musicDTO.getThumbnailPath()) != null) {
                throw new CustomException("Thumbnail already exists");
            }
        }

        Music updatedMusic = Music.builder()
                .id(musicDTO.getId())
                .fileName(musicDTO.getFileName())
                .musicPath(musicDTO.getMusicPath())
                .thumbnailPath(musicDTO.getThumbnailPath())
                .musicTitle(musicDTO.getMusicTitle())
                .artist(artist)
                .genre(genre)
                .build();

        musicRepo.save(updatedMusic);
    }
}
