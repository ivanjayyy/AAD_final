package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.LikedOrRecentSongDTO;
import com.ijse.gdse73.harmoniq_backend.entity.LikedSong;
import com.ijse.gdse73.harmoniq_backend.entity.Music;
import com.ijse.gdse73.harmoniq_backend.entity.User;
import com.ijse.gdse73.harmoniq_backend.repo.LikedSongRepo;
import com.ijse.gdse73.harmoniq_backend.repo.MusicRepo;
import com.ijse.gdse73.harmoniq_backend.repo.UserRepo;
import com.ijse.gdse73.harmoniq_backend.service.LikedSongService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikedSongServiceImpl implements LikedSongService {
    private final LikedSongRepo likedSongRepo;
    private final UserRepo userRepo;
    private final MusicRepo musicRepo;
    private final ModelMapper modelMapper;

    @Override
    public String addOrRemoveLike(LikedOrRecentSongDTO likedOrRecentSongDTO) {

        User user = userRepo.findById(likedOrRecentSongDTO.getUserId()).orElseThrow(
                () -> new UsernameNotFoundException(likedOrRecentSongDTO.getUserId() + " is not valid"));

        Music music = musicRepo.findById(likedOrRecentSongDTO.getMusicId()).orElseThrow(
                () -> new UsernameNotFoundException(likedOrRecentSongDTO.getMusicId() + " is not valid"));

        Optional<LikedSong> existingLike =
                likedSongRepo.findByUserIdAndMusicId(user.getId(), music.getId());

        if(existingLike.isPresent()) {

            // Unlike
            likedSongRepo.delete(existingLike.get());
            return "Unliked Song";

        } else {

            // Like
            LikedSong likedSong = LikedSong.builder()
                    .user(user)
                    .music(music)
                    .build();

            likedSongRepo.save(likedSong);
            return "Liked Song";
        }
    }

    @Override
    public List<LikedOrRecentSongDTO> getLikedSongsByUser(Long userId) {
        if (userId == null) {
            throw new UsernameNotFoundException("User ID is null");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId + " is not valid"));

        return likedSongRepo.findAllByUser(user)
                .stream()
                .map(likedSong -> {
                    LikedOrRecentSongDTO dto = new LikedOrRecentSongDTO();
                    dto.setUserId(likedSong.getUser().getId());
                    dto.setMusicId(likedSong.getMusic().getId());
                    return dto;
                })
                .toList();
    }
}
