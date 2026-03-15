package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Music;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.repo.MusicRepo;
import com.ijse.gdse73.harmoniq_backend.service.MusicService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {
    private final MusicRepo musicRepo;
    private final ModelMapper modelMapper;

    @Override
    public void saveMusic(MusicDTO musicDTO){
        if (musicDTO == null) {
            throw new CustomException("MusicDTO is null");
        }
        musicRepo.save(modelMapper.map(musicDTO, Music.class));
    }

    @Override
    public MusicDTO getMusicById(Long id) {
        return musicRepo.findById(id).map(music -> modelMapper.map(music, MusicDTO.class)).orElse(null);
    }

    @Override
    public List<MusicDTO> getAllMusic() {
        return musicRepo.findAll()
                .stream()
                .map(music -> modelMapper.map(music, MusicDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public Music deleteMusic(Long id) {
        Music music = musicRepo.findById(id).orElseThrow(() -> new RuntimeException("Music not found"));
        musicRepo.deleteById(id);
        return music;
    }

    @Override
    public void updateMusic(MusicDTO musicDTO) {
        if (musicDTO == null) {
            throw new CustomException("MusicDTO is null");
        }
        musicRepo.save(modelMapper.map(musicDTO, Music.class));
    }
}
