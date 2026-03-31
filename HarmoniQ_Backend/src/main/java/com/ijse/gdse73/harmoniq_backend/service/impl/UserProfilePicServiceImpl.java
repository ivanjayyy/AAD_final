package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.UserProfilePicDTO;
import com.ijse.gdse73.harmoniq_backend.entity.User;
import com.ijse.gdse73.harmoniq_backend.entity.UserProfilePic;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.repo.UserProfilePicRepo;
import com.ijse.gdse73.harmoniq_backend.repo.UserRepo;
import com.ijse.gdse73.harmoniq_backend.service.UserProfilePicService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfilePicServiceImpl implements UserProfilePicService {
    private final UserProfilePicRepo userProfilePicRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public UserProfilePicDTO findProfilePic(Long id) {
        User user = userRepo.findById(id).orElseThrow(
                () -> new CustomException("User not found")
        );

        UserProfilePic userProfilePic = userProfilePicRepo.findByUser(user);
        UserProfilePicDTO userProfilePicDTO = new UserProfilePicDTO();
        userProfilePicDTO.setUserId(id);
        userProfilePicDTO.setProfilePic(userProfilePic.getPicUrl());
        return userProfilePicDTO;
    }

    @Override
    public void uploadProfilePic(UserProfilePicDTO userProfilePicDTO) {
        User user = userRepo.findById(userProfilePicDTO.getUserId()).orElseThrow(
                () -> new CustomException("User not found")
        );

        UserProfilePic existing = userProfilePicRepo.findByUser(user);

        if (existing != null) {
            // update existing
            existing.setPicUrl(userProfilePicDTO.getProfilePic());
            userProfilePicRepo.save(existing);

        } else {
            // create new
            UserProfilePic newPic = UserProfilePic.builder()
                    .user(user)
                    .picUrl(userProfilePicDTO.getProfilePic())
                    .build();

            userProfilePicRepo.save(newPic);
        }
    }
}
