package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.SignInDTO;
import com.ijse.gdse73.harmoniq_backend.dto.SignUpDTO;
import com.ijse.gdse73.harmoniq_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("sign-up")
    public ResponseEntity<APIResponse> registerUser(@RequestBody SignUpDTO registerDTO){
        return ResponseEntity.ok(new APIResponse(
                200,"OK",authService.register(registerDTO)
        ));
    }

    @PostMapping("sign-in")
    public ResponseEntity<APIResponse> loginUser(@RequestBody SignInDTO authDTO){
        return ResponseEntity.ok(new APIResponse(
                200,"OK",authService.authenticate(authDTO)
        ));
    }
}
