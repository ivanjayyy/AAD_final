package com.ijse.gdse73.harmoniq_backend.service;

import com.ijse.gdse73.harmoniq_backend.dto.AuthResponseDTO;
import com.ijse.gdse73.harmoniq_backend.dto.ResetPasswordDTO;
import com.ijse.gdse73.harmoniq_backend.dto.SignInDTO;
import com.ijse.gdse73.harmoniq_backend.dto.SignUpDTO;
import jakarta.validation.Valid;

public interface AuthService {
    AuthResponseDTO signIn(SignInDTO signInDTO);
    String signUp(SignUpDTO signUpDTO);
    String resetPassword(@Valid ResetPasswordDTO resetPasswordDTO);
}
