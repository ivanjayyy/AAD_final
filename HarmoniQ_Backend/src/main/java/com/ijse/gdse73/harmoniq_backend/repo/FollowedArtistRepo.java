package com.ijse.gdse73.harmoniq_backend.repo;

import com.ijse.gdse73.harmoniq_backend.entity.FollowedArtist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowedArtistRepo extends JpaRepository<FollowedArtist, Long> {
    Optional<FollowedArtist> findByUserIdAndArtistId(Long userId, Long artistId);

    List<FollowedArtist> findAllByUserId(Long userId);
}
