package com.ijse.gdse73.harmoniq_backend.repo;

import com.ijse.gdse73.harmoniq_backend.entity.Artist;
import com.ijse.gdse73.harmoniq_backend.entity.FollowedArtist;
import com.ijse.gdse73.harmoniq_backend.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowedArtistRepo extends JpaRepository<FollowedArtist, Long> {
    Optional<FollowedArtist> findByUserIdAndArtistId(Long userId, Long artistId);

    List<FollowedArtist> findAllByUser(User user);

    @Query("SELECT fa.artist FROM FollowedArtist fa GROUP BY fa.artist ORDER BY COUNT(fa.user) DESC")
    List<Artist> findTopFollowedArtists(Pageable pageable);
}
