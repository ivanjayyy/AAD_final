package com.ijse.gdse73.harmoniq_backend.service.email;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    // For a simple demo, use a Map. For production, use Redis with an expiration time.
    private final Map<String, String> otpCache = new ConcurrentHashMap<>();

    public String generateOtp(String email) {
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000));
        otpCache.put(email, otp);
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        String cachedOtp = otpCache.get(email);
        if (cachedOtp != null && cachedOtp.equals(otp)) {
            otpCache.remove(email); // Clear OTP after successful use
            return true;
        }
        return false;
    }
}
