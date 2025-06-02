package com.example.emsuser.repository;

import com.example.emsuser.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpTokenRepository extends JpaRepository<OtpToken, UUID> {
    Optional<OtpToken> findByUserIdAndOtpAndUsedFalse(UUID userId, String otp);
}
