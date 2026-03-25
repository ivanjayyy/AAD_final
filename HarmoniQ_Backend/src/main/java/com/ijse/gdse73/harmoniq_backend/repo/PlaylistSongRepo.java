package com.ijse.gdse73.harmoniq_backend.repo;

import com.ijse.gdse73.harmoniq_backend.entity.Music;
import com.ijse.gdse73.harmoniq_backend.entity.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface PlaylistSongRepo extends JpaRepository<PlaylistSong, Long> {
    List<PlaylistSong> getPlaylistSongsByPlaylistId(Long playlistId);
    Optional<PlaylistSong> findByPlaylistIdAndMusicId(Long playlistId, Long musicId);
    List<PlaylistSong> findMusicByPlaylistIdIn(List<Long> playlistIds);
}
