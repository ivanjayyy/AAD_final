package com.ijse.gdse73.harmoniq_backend.repo;

import com.ijse.gdse73.harmoniq_backend.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepo extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmail(String email);
    void deleteByEmail(String email);
}