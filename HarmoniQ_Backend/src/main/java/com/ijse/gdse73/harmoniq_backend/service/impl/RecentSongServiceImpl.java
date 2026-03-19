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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

//@Service
//@RequiredArgsConstructor
//public class RecentSongServiceImpl implements RecentSongService {
//
//    private final UserRepo userRepo;
//    private final MusicRepo musicRepo;
//
//    @Override
//    @Transactional
//    public void addRecentSong(LikedOrRecentSongDTO dto) {
//
//        User user = userRepo.findById(dto.getUserId())
//                .orElseThrow(() ->
//                        new UsernameNotFoundException(dto.getUserId() + " is not valid"));
//
//        Music music = musicRepo.findById(dto.getMusicId())
//                .orElseThrow(() ->
//                        new RuntimeException(dto.getMusicId() + " is not valid"));
//
//        List<RecentSong> recentSongs = user.getRecentSongs();
//
//        // Remove duplicate song if exists
//        recentSongs.removeIf(song ->
//                song.getMusic().getId().equals(music.getId()));
//
//        // Create new recent song
//        RecentSong newRecentSong = RecentSong.builder()
//                .user(user)
//                .music(music)
//                .build();
//
//        // Add to first position
//        recentSongs.add(0, newRecentSong);
//
//        // Keep max 10 songs
//        if (recentSongs.size() > 10) {
//            recentSongs.remove(10);
//        }
//
//        userRepo.save(user);
//    }
//
//    @Override
//    public List<MusicDTO> loadRecentSongs(Long userId) {
//        if (userId == null) {
//            throw new CustomException("User ID is null");
//        }
//
//        User user = userRepo.findById(userId)
//                .orElseThrow(() ->
//                        new UsernameNotFoundException(userId + " is not valid"));
//
//        return user.getRecentSongs().stream()
//                .map(recentSong -> recentSong.getMusic())
//                .map(music -> new ModelMapper().map(music, MusicDTO.class))
//                .toList();
//    }
//}

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
        User user = userRepo.findById(likedOrRecentSongDTO.getUserId()).orElseThrow(
                () -> new UsernameNotFoundException(likedOrRecentSongDTO.getUserId() + " is not valid"));

        Music music = musicRepo.findById(likedOrRecentSongDTO.getMusicId()).orElseThrow(
                () -> new UsernameNotFoundException(likedOrRecentSongDTO.getMusicId() + " is not valid"));

        // 1. Copy existing list
        List<RecentSong> oldList = new ArrayList<>(user.getRecentSongs());

        // 2. Remove duplicates (same music)
        oldList.removeIf(song -> song.getMusic().getId().equals(music.getId()));

        // 3. CLEAR existing list (this deletes from DB because of orphanRemoval)
        user.getRecentSongs().clear();

        // 4. Create new list (Deque for ordering)
        Deque<RecentSong> newList = new ArrayDeque<>();

        // 5. Add new song to front
        RecentSong newRecent = RecentSong.builder()
                .user(user)
                .music(music)
                .build();

        newList.addFirst(newRecent);

        // 6. Add previous songs
        for (RecentSong song : oldList) {
            song.setUser(user); // reattach
            newList.addLast(song);
        }

        // 7. Trim to max 10
        while (newList.size() > 10) {
            newList.removeLast();
        }

        // 8. Set back to user
        user.getRecentSongs().addAll(newList);

        // 9. Save (cascade will insert new records)
        userRepo.save(user);


//        User user = userRepo.findById(likedOrRecentSongDTO.getUserId()).orElseThrow(
//                () -> new UsernameNotFoundException(likedOrRecentSongDTO.getUserId() + " is not valid"));
//
//        Music music = musicRepo.findById(likedOrRecentSongDTO.getMusicId()).orElseThrow(
//                () -> new UsernameNotFoundException(likedOrRecentSongDTO.getMusicId() + " is not valid"));
//
//        RecentSong recentSong = RecentSong.builder()
//                .user(user)
//                .music(music)
//                .build();
//
//        List<RecentSong> recentSongList = user.getRecentSongs();
//        Deque<RecentSong> recentSongs = new ArrayDeque<>(recentSongList);
//
//        recentSongs.removeIf(song -> song.getMusic().getId().equals(music.getId()));
//        recentSongs.addFirst(recentSong);
//
//        if (recentSongs.size() > 10) {
//            recentSongRepo.delete(recentSongs.getLast());
//            recentSongs.removeLast();
//        }
//        recentSongList.clear();
//        recentSongList.addAll(recentSongs);
//
//        user.setRecentSongs(recentSongList);
//        userRepo.save(user);
    }

    @Override
    public List<MusicDTO> loadRecentSongs(Long userId) {
        if (userId == null) {
            throw new CustomException("User ID is null");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException(userId + " is not valid"));

        return user.getRecentSongs().stream()
                .map(recentSong -> recentSong.getMusic())
                .map(music -> new ModelMapper().map(music, MusicDTO.class))
                .toList();
    }
}
