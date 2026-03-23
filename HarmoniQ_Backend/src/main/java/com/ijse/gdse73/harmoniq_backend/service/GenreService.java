package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.GenreDTO;

import java.util.List;

public interface GenreService {
    void addNewGenre(GenreDTO genreDTO);

    List<GenreDTO> getAllGenre();
}
