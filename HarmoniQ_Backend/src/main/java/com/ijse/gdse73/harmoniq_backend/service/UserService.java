package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.UserDTO;
import com.ijse.gdse73.harmoniq_backend.entity.User;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    void deleteUser(Long id);
    void updateUser(UserDTO userDTO);
    UserDTO getUser(String username);
}
