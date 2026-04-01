package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.LikedOrRecentSongDTO;
import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Music;
import com.ijse.gdse73.harmoniq_backend.entity.RecentSong;
import com.ijse.gdse73.harmoniq_backend.entity.User;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.repo.MusicRepo;
import com.ijse.gdse73.harmoniq_backend.repo.RecentSongRepo;
import com.ijse.gdse73.harmoniq_backend.repo.UserRepo;
import com.ijse.gdse73.harmoniq_backend.service.RecentSongService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentSongServiceImpl implements RecentSongService {
    private final RecentSongRepo recentSongRepo;
    private final UserRepo userRepo;
    private final MusicRepo musicRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void addRecentSong(LikedOrRecentSongDTO likedOrRecentSongDTO) {
        Long userId = likedOrRecentSongDTO.getUserId();
        Long musicId = likedOrRecentSongDTO.getMusicId();

        RecentSong recentSong = recentSongRepo
                .findByUserIdAndMusicId(userId, musicId)
                .orElse(null);

        if (recentSong != null) {
            recentSong.setPlayedAt(LocalDateTime.now());

        } else {
            recentSong = new RecentSong();
            recentSong.setUser(userRepo.findUserById(userId));
            recentSong.setMusic(musicRepo.findById(musicId).orElseThrow());
            recentSong.setPlayedAt(LocalDateTime.now());
        }

        recentSongRepo.save(recentSong);

        List<RecentSong> list = recentSongRepo.findByUserIdOrderByPlayedAtDesc(userId);

        if (list.size() > 6) {
            recentSongRepo.deleteAll(list.subList(6, list.size()));
        }
    }

    private MusicDTO convertToDto(Music music) {
        MusicDTO musicDTO = modelMapper.map(music, MusicDTO.class);
        if (music.getArtist() != null) {
            musicDTO.setMusicArtist(music.getArtist().getName());
        }
        return musicDTO;
    }

    @Override
    public List<MusicDTO> loadRecentSongs(Long userId) {
        if (userId == null) {
            throw new CustomException("User ID is empty");
        }

        List<RecentSong> list = recentSongRepo.findByUserIdOrderByPlayedAtDesc(userId);

        return list.stream()
                .map(RecentSong::getMusic)
                .map(this::convertToDto)
                .toList();
    }
}
