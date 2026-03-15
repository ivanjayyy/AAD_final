package com.ijse.gdse73.harmoniq_backend.service.impl;

import com.ijse.gdse73.harmoniq_backend.dto.SignInDTO;
import com.ijse.gdse73.harmoniq_backend.dto.AuthResponseDTO;
import com.ijse.gdse73.harmoniq_backend.dto.SignUpDTO;
import com.ijse.gdse73.harmoniq_backend.entity.Role;
import com.ijse.gdse73.harmoniq_backend.entity.User;
import com.ijse.gdse73.harmoniq_backend.exception.CustomException;
import com.ijse.gdse73.harmoniq_backend.repo.UserRepo;
import com.ijse.gdse73.harmoniq_backend.service.AuthService;
import com.ijse.gdse73.harmoniq_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponseDTO signIn(SignInDTO signInDTO){
        // Find user by username from database
        User user = userRepository.findByUsername(signInDTO.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException(signInDTO.getUsername()));

        // Match passwords (DB and Request)
        if (!passwordEncoder.matches(signInDTO.getPassword(),user.getPassword())){
            throw new BadCredentialsException(user.getUsername() + " is not valid");
        }

        // Generate new token
        String token = jwtUtil.generateToken(user.getId(), signInDTO.getUsername(), String.valueOf(user.getRole()));
        return new AuthResponseDTO(token);
    }

    public String signUp(SignUpDTO signUpDTO){
        if (userRepository.findByUsername(signUpDTO.getUsername()).isPresent()){
            throw new CustomException("Username already exists!");
        }

        User user = User.builder()
                .username(signUpDTO.getUsername())
                .email(signUpDTO.getEmail())
                .password(passwordEncoder.encode(signUpDTO.getPassword()))
                .role(Role.valueOf(signUpDTO.getRole()))
                .build();

        userRepository.save(user);
        return "User registered successfully!";
    }
}
