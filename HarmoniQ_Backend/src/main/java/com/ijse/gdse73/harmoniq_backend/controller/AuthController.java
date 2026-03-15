package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.SignInDTO;
import com.ijse.gdse73.harmoniq_backend.dto.SignUpDTO;
import com.ijse.gdse73.harmoniq_backend.service.AuthService;
import jakarta.validation.Valid;
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
    public ResponseEntity<APIResponse> userSignUp(@RequestBody @Valid SignUpDTO signUpDTO){
        return ResponseEntity.ok(new APIResponse(
                200,"User Sign-up Successfully",authService.signUp(signUpDTO)
        ));
    }

    @PostMapping("sign-in")
    public ResponseEntity<APIResponse> userSignIn(@RequestBody @Valid SignInDTO SignInDTO){
        return ResponseEntity.ok(new APIResponse(
                200,"User Sign-in Successfully",authService.signIn(SignInDTO)
        ));
    }
}
