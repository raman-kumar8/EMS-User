package com.example.emsuser.service;

import com.example.emsuser.dto.OtpDto;
import com.example.emsuser.model.OtpToken;
import com.example.emsuser.repository.OtpTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OtpService {
    @Autowired
    OtpTokenRepository otpTokenRepository;

    public void  SaveOto(OtpToken otpToken){
        otpTokenRepository.save(otpToken);

    }
    public Optional<OtpToken> findByUserIdAndOtpAndUsed(UUID userId, OtpDto otpDto){
        return otpTokenRepository.findByUserIdAndOtpAndUsedFalse(userId,otpDto.getOtp());
    }
}
