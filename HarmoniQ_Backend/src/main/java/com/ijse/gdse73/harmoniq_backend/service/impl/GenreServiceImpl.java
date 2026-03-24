package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.GenreDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Genre;
import com.ijse.gdse73.harmoniq_backend.repo.GenreRepo;
import com.ijse.gdse73.harmoniq_backend.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepo genreRepo;
    private final ModelMapper modelMapper;

    @Override
    public void addNewGenre(GenreDTO genreDTO) {
        if (genreDTO == null){
            throw new NullPointerException("GenreDTO cannot be null");
        }

        if (genreRepo.findGenreByName(genreDTO.getName()) != null){
            return;
        }

        genreRepo.save(modelMapper.map(genreDTO, Genre.class));
    }

    @Override
    public List<GenreDTO> getAllGenre() {
        return genreRepo.findAll()
                .stream()
                .map(genre -> modelMapper.map(genre, GenreDTO.class))
                .toList();
    }
}
