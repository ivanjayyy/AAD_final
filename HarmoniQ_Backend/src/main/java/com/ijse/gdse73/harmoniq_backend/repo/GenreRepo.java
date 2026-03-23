package com.ijse.gdse73.harmoniq_backend.repo;

import com.ijse.gdse73.harmoniq_backend.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepo extends JpaRepository<Genre, Long> {
    Genre findGenreById(Long musicGenreId);
}
