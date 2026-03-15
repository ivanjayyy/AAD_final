package com.ijse.gdse73.harmoniq_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInDTO {

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}