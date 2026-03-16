package com.ijse.gdse73.harmoniq_backend.repo;

import com.ijse.gdse73.harmoniq_backend.entity.Music;
import com.ijse.gdse73.harmoniq_backend.entity.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistSongRepo extends JpaRepository<PlaylistSong, Long> {
    List<PlaylistSong> getPlaylistSongsByPlaylistId(Long playlistId);
}
