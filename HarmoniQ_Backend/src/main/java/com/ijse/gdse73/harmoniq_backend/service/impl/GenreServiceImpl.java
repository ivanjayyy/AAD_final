package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.GenreDTO;
import com.ijse.gdse73.harmoniq_backend.dto.GenreWithTrackCountDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Genre;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
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
            throw new CustomException("Genre already exists");
        }

        genreRepo.save(modelMapper.map(genreDTO, Genre.class));
    }

    @Override
    public List<GenreWithTrackCountDTO> getAllGenre() {
        return genreRepo.findAll()
                .stream()
                .map(genre -> {
                    GenreDTO genreDTO = modelMapper.map(genre, GenreDTO.class);
                    GenreWithTrackCountDTO dto = modelMapper.map(genreDTO, GenreWithTrackCountDTO.class);
                    dto.setTrackCount(genre.getMusics() != null ? genre.getMusics().size() : 0);
                    return dto;
                })
                .toList();
    }

    @Override
    public String deleteGenre(Long id) {
        if (genreRepo.findById(id).isPresent()){
            genreRepo.deleteById(id);
            return "Genre deleted successfully";
        } else {
            return "Genre not found";
        }
    }

    @Override
    public void updateGenre(Long id, GenreDTO genreDTO) {
        if (genreDTO == null){
            throw new NullPointerException("Genre new datalist is empty");
        }

        Genre genre = genreRepo.findById(id).orElseThrow(() -> new CustomException("Genre not found"));

        if (!genre.getName().equals(genreDTO.getName())){
            if (genreRepo.findGenreByName(genreDTO.getName()) != null){
                throw new CustomException("Genre already exists");
            }
        }

        genre.setName(genreDTO.getName());
        genreRepo.save(genre);
    }
}
