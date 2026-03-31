package com.ijse.gdse73.harmoniq_backend.service.email;

import com.ijse.gdse73.harmoniq_backend.entity.Otp;
import com.ijse.gdse73.harmoniq_backend.repo.OtpRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepo otpRepo;
    private final PasswordEncoder passwordEncoder;

    // OTP validity in seconds
    private static final long OTP_VALIDITY_SECONDS = 60;

    // Generate and save OTP
    public String generateOtp(String email) {
        // Generate random 6-digit OTP
        String rawOtp = String.valueOf(100000 + new Random().nextInt(900000));
        String encodedOtp = passwordEncoder.encode(rawOtp);

        LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(OTP_VALIDITY_SECONDS);

        // Save or update OTP
        Otp otpEntity = Otp.builder()
                .email(email)
                .otp(encodedOtp)
                .expiryTime(expiryTime)
                .build();

        otpRepo.deleteByEmail(email); // remove old OTP if exists
        otpRepo.save(otpEntity);

        return rawOtp; // send raw OTP to user
    }

    // Validate OTP
    public boolean validateOtp(String email, String otp) {
        return otpRepo.findByEmail(email)
                .map(otpEntity -> {
                    // Check expiry
                    if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
                        otpRepo.delete(otpEntity);
                        return false;
                    }

                    // Check match
                    boolean matches = passwordEncoder.matches(otp, otpEntity.getOtp());
                    if (matches) otpRepo.delete(otpEntity); // remove after successful verification
                    return matches;
                })
                .orElse(false);
    }

    @Scheduled(fixedRate = 60000) // 60,000 ms = 1 min
    public void removeExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        otpRepo.findAll().stream()
                .filter(otp -> otp.getExpiryTime().isBefore(now))
                .forEach(otpRepo::delete);
    }
}