package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.ArtistDTO;
import com.ijse.gdse73.harmoniq_backend.dto.FollowedArtistDTO;
import com.ijse.gdse73.harmoniq_backend.dto.MusicDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Artist;
import com.ijse.gdse73.harmoniq_backend.entity.FollowedArtist;
import com.ijse.gdse73.harmoniq_backend.entity.Music;
import com.ijse.gdse73.harmoniq_backend.entity.User;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.repo.ArtistRepo;
import com.ijse.gdse73.harmoniq_backend.repo.FollowedArtistRepo;
import com.ijse.gdse73.harmoniq_backend.repo.MusicRepo;
import com.ijse.gdse73.harmoniq_backend.repo.UserRepo;
import com.ijse.gdse73.harmoniq_backend.service.FollowedArtistService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowedArtistServiceImpl implements FollowedArtistService {
    private final FollowedArtistRepo followedArtistRepo;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;
    private final ArtistRepo artistRepo;
    private final MusicRepo musicRepo;

    @Override
    public String addFollowedArtist(FollowedArtistDTO followedArtistDTO) {
        User user = userRepo.findById(followedArtistDTO.getUserId()).orElseThrow(
                () -> new CustomException("User not found")
        );
        Artist artist = artistRepo.findById(followedArtistDTO.getArtistId()).orElseThrow(
                () -> new CustomException("Artist not found")
        );

        Optional<FollowedArtist> existingFollowedArtist =
                followedArtistRepo.findByUserIdAndArtistId(followedArtistDTO.getUserId(),followedArtistDTO.getArtistId());

        if(existingFollowedArtist.isPresent()) {
            followedArtistRepo.delete(existingFollowedArtist.get());
            return "Unfollowed Artist";
        } else {
            FollowedArtist followedArtist = FollowedArtist.builder()
                    .user(user)
                    .artist(artist)
                    .build();

            followedArtistRepo.save(followedArtist);
            return "Followed Artist";
        }
    }

    @Override
    public String checkFollowStatus(FollowedArtistDTO followedArtistDTO) {

        Optional<FollowedArtist> existingFollowedArtist =
                followedArtistRepo.findByUserIdAndArtistId(followedArtistDTO.getUserId(),followedArtistDTO.getArtistId());

        if(existingFollowedArtist.isPresent()) {
            return "Following";
        } else {
            return "Not Following";
        }
    }

    @Override
    public List<MusicDTO> getRandomFollowedArtistMusic(Long userId, int artistCount, int songsPerArtist) {

        User user = userRepo.findById(userId).orElseThrow(
                () -> new CustomException("User not found")
        );

        // 1️⃣ Get all followed artists of the user
        List<FollowedArtist> followedArtists = followedArtistRepo.findAllByUser(user);

        if(followedArtists.isEmpty()) return Collections.emptyList();

        // 2️⃣ Shuffle and pick random artists
        Collections.shuffle(followedArtists);
        List<FollowedArtist> randomArtists = followedArtists.stream()
                .limit(artistCount)
                .toList();

        List<Music> randomMusicList = new ArrayList<>();

        // 3️⃣ For each selected artist, fetch their songs from MusicRepo and pick random songs
        for(FollowedArtist fa : randomArtists){
            Artist artist = fa.getArtist();
            List<Music> artistMusic = musicRepo.findAllByArtist(artist);

            if(artistMusic.isEmpty()) continue;

            Collections.shuffle(artistMusic);
            randomMusicList.addAll(
                    artistMusic.stream()
                            .limit(songsPerArtist)
                            .toList()
            );
        }

        // 4️⃣ Shuffle final combined list
        Collections.shuffle(randomMusicList);

        return randomMusicList.stream()
                .map(music -> modelMapper.map(music, MusicDTO.class)).toList();
    }

    @Override
    public List<ArtistDTO> getFamousArtists() {
        Pageable topThree = PageRequest.of(0, 3);

        List<Artist> famousArtists = followedArtistRepo.findTopFollowedArtists(topThree);

        return famousArtists.stream()
                .map(artist -> modelMapper.map(artist, ArtistDTO.class))
                .toList();
    }

    @Override
    public List<ArtistDTO> getAllFollowingArtists(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(
                () -> new CustomException("User not found")
        );

        return followedArtistRepo.findAllByUser(user)
                .stream()
                .map(FollowedArtist::getArtist)
                .map(this::convertToDto)
                .toList();
    }

    private ArtistDTO convertToDto(Artist artist) {
        return modelMapper.map(artist, ArtistDTO.class);
    }
}
