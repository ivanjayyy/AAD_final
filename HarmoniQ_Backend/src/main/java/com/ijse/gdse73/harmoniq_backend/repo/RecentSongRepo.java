package com.ijse.gdse73.harmoniq_backend.repo;

import com.ijse.gdse73.harmoniq_backend.entity.RecentSong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentSongRepo extends JpaRepository<RecentSong, Long> {
}
