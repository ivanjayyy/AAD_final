package com.ijse.gdse73.harmoniq_backend.repo;

import com.ijse.gdse73.harmoniq_backend.entity.RecentSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecentSongRepo extends JpaRepository<RecentSong, Long> {
    Optional<RecentSong> findByUserIdAndMusicId(Long userId, Long musicId);

    List<RecentSong> findByUserIdOrderByPlayedAtDesc(Long userId);
}
