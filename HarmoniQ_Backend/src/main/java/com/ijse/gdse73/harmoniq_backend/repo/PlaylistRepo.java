package com.ijse.gdse73.harmoniq_backend.repo;

import com.ijse.gdse73.harmoniq_backend.entity.Playlist;
import com.ijse.gdse73.harmoniq_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface PlaylistRepo extends JpaRepository<Playlist, Long> {
    List<Playlist> getAllByUsername(String username);

    boolean getPlaylistByUsernameAndPlaylistName(String username, String playlistName);
}
