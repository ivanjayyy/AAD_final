package com.ijse.gdse73.harmoniq_backend.repo;

import com.ijse.gdse73.harmoniq_backend.entity.LikedSong;
import com.ijse.gdse73.harmoniq_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikedSongRepo extends JpaRepository<LikedSong, Long> {
    Optional<LikedSong> findByUserIdAndMusicId(Long userId, Long musicId);
    List<LikedSong> findAllByUser(User user);
}
