package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;
import com.ijse.gdse73.harmoniq_backend.dto.PlaylistSongDTO;

import java.util.List;

public interface PlaylistSongService {
    List<MusicDTO> getAllPlaylistSongs(Long playlistId);

    void addNewPlaylistSong(PlaylistSongDTO playlistSongDTO);

    void removePlaylistSong(PlaylistSongDTO playlistSongDTO);
}
