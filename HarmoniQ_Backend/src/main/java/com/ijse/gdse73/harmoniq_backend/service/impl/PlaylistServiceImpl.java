package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;
import com.ijse.gdse73.harmoniq_backend.dto.PlaylistDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Music;
import com.ijse.gdse73.harmoniq_backend.entity.Playlist;
import com.ijse.gdse73.harmoniq_backend.entity.User;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.repo.PlaylistRepo;
import com.ijse.gdse73.harmoniq_backend.repo.PlaylistSongRepo;
import com.ijse.gdse73.harmoniq_backend.repo.UserRepo;
import com.ijse.gdse73.harmoniq_backend.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistRepo playlistRepo;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;
    private final PlaylistSongRepo playlistSongRepo;

    @Override
    public void createPlaylist(PlaylistDTO playlistDTO) {
        if (playlistDTO == null) {
            throw new CustomException("Playlist datalist is empty");
        }

        User user = userRepo.findUserById(playlistDTO.getUserId());

        if (playlistRepo.findByUserAndPlaylistName(user,playlistDTO.getPlaylistName())) {
            throw new CustomException("Playlist already exists");
        }

        Playlist playlist = Playlist.builder()
                .playlistName(playlistDTO.getPlaylistName())
                .user(user)
                .build();

        playlistRepo.save(playlist);
    }

    @Override
    public List<PlaylistDTO> getPlaylistsByUserId(Long userId) {
        if (userId == null) {
            throw new CustomException("Username is null");
        }

        User user = userRepo.findUserById(userId);

        return playlistRepo.findAllByUser(user)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    private PlaylistDTO convertToDto(Playlist playlist) {

        modelMapper.typeMap(Playlist.class, PlaylistDTO.class)
                .addMappings(mapper -> {
                    mapper.skip(PlaylistDTO::setUserId);
                });

        PlaylistDTO playlistDTO = modelMapper.map(playlist, PlaylistDTO.class);

        if (playlist.getUser() != null) {
            playlistDTO.setUserId(playlist.getUser().getId());
        }

        return playlistDTO;
    }

    @Override
    public void deletePlaylist(Long id) {
        playlistRepo.deleteById(id);
    }

    @Override
    public void updatePlaylist(Long playlistId, PlaylistDTO playlistDTO) {
        Playlist playlist = playlistRepo.findById(playlistId).orElseThrow(() -> new CustomException("Playlist not found"));

        if (!playlist.getPlaylistName().equals(playlistDTO.getPlaylistName())) {
            if (playlistRepo.findByPlaylistName(playlistDTO.getPlaylistName()) != null) {
                throw new CustomException("Playlist already exists");
            }
        }

        playlist.setPlaylistName(playlistDTO.getPlaylistName());
        playlistRepo.save(playlist);
    }

    @Override
    public List<MusicDTO> getSongsFromPlaylists(Long userId) {
        if (userId == null) {
            throw new CustomException("Username is null");
        }

        User user = userRepo.findUserById(userId);

        List<Playlist> playlists = playlistRepo.findAllByUser(user);

        List<Long> playlistIds = playlists.stream()
                .map(Playlist::getId)
                .toList();

        if (playlistIds.isEmpty()) {
            return List.of();
        }

        return playlistSongRepo.findMusicByPlaylistIdIn(playlistIds)
                .stream()
                .distinct() // Prevent duplicates
                .limit(10)
                .map(music -> modelMapper.map(music, MusicDTO.class))
                .toList();
    }

    @Override
    public Long getPlaylistId(String playlistName) {
        Playlist playlist = playlistRepo.findByPlaylistName(playlistName);
        return playlist.getId();
    }
}
