package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.GenreDTO;
import com.ijse.gdse73.harmoniq_backend.dto.GenreWithTrackCountDTO;

import java.util.List;

public interface GenreService {
    void addNewGenre(GenreDTO genreDTO);

    List<GenreWithTrackCountDTO> getAllGenre();

    String deleteGenre(Long id);

    void updateGenre(Long id, GenreDTO genreDTO);
}
