package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.AuthResponseDTO;
import com.ijse.gdse73.harmoniq_backend.dto.SignInDTO;
import com.ijse.gdse73.harmoniq_backend.dto.SignUpDTO;

public interface AuthService {
    AuthResponseDTO authenticate(SignInDTO signInDTO);
    String register(SignUpDTO signUpDTO);
}
