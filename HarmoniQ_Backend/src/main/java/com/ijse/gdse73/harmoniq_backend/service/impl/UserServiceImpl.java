package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.UserDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Music;
import com.ijse.gdse73.harmoniq_backend.entity.Role;
import com.ijse.gdse73.harmoniq_backend.entity.User;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.repo.UserRepo;
import com.ijse.gdse73.harmoniq_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();
    }

    @Override
    public void deleteUser(Long id) {
        userRepo.findById(id).orElseThrow(() -> new CustomException("User not found"));
        userRepo.deleteById(id);
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        User user = userRepo.findById(userDTO.getId()).orElseThrow(() -> new CustomException("User not found"));
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        userRepo.save(user);
    }

    @Override
    public UserDTO getUser(Long id) {
        return userRepo.findById(id).map(user -> modelMapper.map(user, UserDTO.class)).orElseThrow(() -> new CustomException("User not found"));
    }

    @Override
    public void updateUserRole(Long id, String role) {
        User user = userRepo.findById(id).orElseThrow(() -> new CustomException("User not found"));
        user.setRole(Role.valueOf(role));
        userRepo.save(user);
    }
}
