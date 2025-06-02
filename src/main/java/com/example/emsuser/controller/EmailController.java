package com.example.emsuser.controller;

import com.example.emsuser.dto.EmailDetails;
import com.example.emsuser.dto.OtpDto;
import com.example.emsuser.exception.CustomException;
import com.example.emsuser.model.OtpToken;
import com.example.emsuser.model.UserModel;
import com.example.emsuser.repository.EmailService;
import com.example.emsuser.security.JwtTokenProvider;
import com.example.emsuser.service.OtpService;
import com.example.emsuser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserService userService;
   @Autowired
   private OtpService otpService;
    @PostMapping("/sendMail")
    public String sendMail(@RequestBody EmailDetails details)
    {
        String status
                = emailService.sendSimpleMail(details);

        return status;
    }
    @PostMapping("/sendMailWithAttachment")
    public String sendMailWithAttachment(
            @RequestBody EmailDetails details)
    {
        String status
                = emailService.sendMailWithAttachment(details);

        return status;
    }
    @GetMapping("/send-otp")
    public String sendOtp(@CookieValue("jwt_token") String token) {
        UUID userId = jwtTokenProvider.getUserIdFromJWT(token);
        UserModel user = userService.getUserById(userId);

        // Generate 6-digit random OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Store OTP with expiry
        OtpToken otpToken = new OtpToken();
        otpToken.setOtp(otp);
        otpToken.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        otpToken.setUser(user);

         otpService.SaveOto(otpToken);
        // Send email
        EmailDetails details = new EmailDetails();
        details.setRecipient(user.getEmail());
        details.setSubject("Your EMS OTP for Password Reset");
        details.setMsgBody("Here is your OTP to reset the password");

        OtpDto otpDto = new OtpDto();
        otpDto.setOtp(otp);
        otpDto.setExpiryTime(otpToken.getExpiryTime().toString());

        return emailService.sendOtpMail(otpDto, details);
    }
    @PostMapping("/verify-otp")
    public String verifyOtp(@CookieValue("jwt_token") String token, @RequestBody OtpDto otpDto) {
        UUID userId = jwtTokenProvider.getUserIdFromJWT(token);

        OtpToken otpToken = otpService.findByUserIdAndOtpAndUsed(userId,otpDto)
                .orElseThrow(() -> new CustomException("Invalid OTP"));

        if (otpToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new CustomException("OTP expired");
        }

        otpToken.setUsed(true);
        otpService.SaveOto(otpToken);

        return "OTP verified successfully!";
    }



}
