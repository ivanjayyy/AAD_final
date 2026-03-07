package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.PlaylistDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Playlist;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.repo.PlaylistRepo;
import com.ijse.gdse73.harmoniq_backend.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistRepo playlistRepo;
    private final ModelMapper modelMapper;

    @Override
    public void createPlaylist(PlaylistDTO playlistDTO) {
        if (playlistDTO == null) {
            throw new CustomException("PlaylistDTO is null");
        }
        playlistRepo.save(modelMapper.map(playlistDTO, Playlist.class));
    }
}
