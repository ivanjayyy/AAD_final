package com.ijse.gdse73.harmoniq_backend.dto;

import lombok.Data;

@Data
public class SignUpDTO {
    private String username;
    private String email;
    private String password;
    private String role;
}
