package com.ijse.gdse73.harmoniq_backend.controller;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import com.ijse.gdse73.harmoniq_backend.dto.SignInDTO;
import com.ijse.gdse73.harmoniq_backend.dto.SignUpDTO;
import com.ijse.gdse73.harmoniq_backend.dto.ValidateOtpDTO;
import com.ijse.gdse73.harmoniq_backend.service.AuthService;
import com.ijse.gdse73.harmoniq_backend.service.email.EmailService;
import com.ijse.gdse73.harmoniq_backend.service.email.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final EmailService emailService;
    private final OtpService otpService;

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

    @PostMapping("/send-otp")
    public ResponseEntity<APIResponse> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = otpService.generateOtp(email);

        emailService.sendEmail(
                email,
                "HarmoniQ OTP Verification",
                "Your OTP is: " + otp + "\nValid for 60 seconds."
        );

        return ResponseEntity.ok(new APIResponse(
                200, "OTP sent successfully", null
        ));
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<APIResponse> validateOtp(@RequestBody ValidateOtpDTO validateOtpDTO) {
        String email = validateOtpDTO.getEmail();
        String otp = validateOtpDTO.getOtp();

        if (!otpService.validateOtp(email, otp)) {
            return ResponseEntity.badRequest().body(new APIResponse(400, "Invalid OTP", null));
        } else {
            return ResponseEntity.ok(new APIResponse(200, "OTP validated successfully", null));
        }
    }
}
