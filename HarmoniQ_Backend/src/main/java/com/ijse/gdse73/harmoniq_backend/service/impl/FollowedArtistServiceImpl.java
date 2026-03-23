package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.FollowedArtistDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Artist;
import com.ijse.gdse73.harmoniq_backend.entity.FollowedArtist;
import com.ijse.gdse73.harmoniq_backend.entity.User;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.repo.ArtistRepo;
import com.ijse.gdse73.harmoniq_backend.repo.FollowedArtistRepo;
import com.ijse.gdse73.harmoniq_backend.repo.UserRepo;
import com.ijse.gdse73.harmoniq_backend.service.FollowedArtistService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowedArtistServiceImpl implements FollowedArtistService {
    private final FollowedArtistRepo followedArtistRepo;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;
    private final ArtistRepo artistRepo;

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
}
