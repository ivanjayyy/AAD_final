package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;
import com.ijse.gdse73.harmoniq_backend.dto.PlaylistDTO;

import java.util.List;

public interface PlaylistService {
    void createPlaylist(PlaylistDTO playlistDTO);
    List<PlaylistDTO> getPlaylistsByUserId(Long userId);
    void deletePlaylist(Long id);
    void updatePlaylist(PlaylistDTO playlistDTO);

    List<MusicDTO> getSongsFromPlaylists(Long userId);
    Long getPlaylistId(String playlistName);
}
