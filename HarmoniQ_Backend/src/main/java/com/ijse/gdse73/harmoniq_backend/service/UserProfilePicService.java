package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.UserProfilePicDTO;

public interface UserProfilePicService {
    UserProfilePicDTO findProfilePic(Long id);
    void uploadProfilePic(UserProfilePicDTO userProfilePicDTO);
}
