package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;
import com.ijse.gdse73.harmoniq_backend.dto.PlaylistSongDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Music;
import com.ijse.gdse73.harmoniq_backend.entity.Playlist;
import com.ijse.gdse73.harmoniq_backend.entity.PlaylistSong;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.repo.MusicRepo;
import com.ijse.gdse73.harmoniq_backend.repo.PlaylistRepo;
import com.ijse.gdse73.harmoniq_backend.repo.PlaylistSongRepo;
import com.ijse.gdse73.harmoniq_backend.service.PlaylistSongService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistSongServiceImpl implements PlaylistSongService {
    private final PlaylistRepo playlistRepo;
    private final MusicRepo musicRepo;
    private final PlaylistSongRepo playlistSongRepo;
    private final ModelMapper modelMapper;

    @Override
    public List<MusicDTO> getAllPlaylistSongs(Long playlistId) {
        if (playlistId == null) {
            throw new CustomException("Playlist ID is null");
        }

        return playlistSongRepo.getPlaylistSongsByPlaylistId(playlistId)
                .stream()
                .map(PlaylistSong::getMusic)
                .map(this::convertToDto)
                .toList();
    }

    private MusicDTO convertToDto(Music music) {
        MusicDTO musicDTO = modelMapper.map(music, MusicDTO.class);
        if (music.getArtist() != null) {
            musicDTO.setMusicArtist(music.getArtist().getName());
        }
        return musicDTO;
    }

    @Override
    public void addNewPlaylistSong(PlaylistSongDTO playlistSongDTO) {
        Playlist playlist = playlistRepo.findById(playlistSongDTO.getPlaylistId()).orElseThrow(
                () -> new CustomException("Playlist ID is not valid"));

        Music music = musicRepo.findById(playlistSongDTO.getMusicId()).orElseThrow(
                () -> new CustomException("Music ID is not valid"));

        if (playlistSongRepo.findByPlaylistAndMusic(playlist,music)) {
            throw new CustomException("This song is already in the playlist.");
        }

        PlaylistSong playlistSong = PlaylistSong.builder()
                .playlist(playlist)
                .music(music)
                .build();

        playlistSongRepo.save(playlistSong);
    }

    @Override
    @Transactional // Ensures the delete operation is committed safely
    public void removePlaylistSong(PlaylistSongDTO playlistSongDTO) {
        Long playlistId = playlistSongDTO.getPlaylistId();
        Long musicId = playlistSongDTO.getMusicId();

        // 1. Find the specific relationship entity
        PlaylistSong playlistSong = playlistSongRepo.findByPlaylistIdAndMusicId(playlistId, musicId)
                .orElseThrow(() -> new CustomException("This song is not in the specified playlist."));

        // 2. Delete the entity from the table
        playlistSongRepo.delete(playlistSong);
    }
}
