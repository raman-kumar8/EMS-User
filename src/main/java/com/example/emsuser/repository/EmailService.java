package com.example.emsuser.repository;

import com.example.emsuser.dto.EmailDetails;
import com.example.emsuser.dto.OtpDto;

public interface EmailService  {
    String sendSimpleMail(EmailDetails details);
    String sendMailWithAttachment(EmailDetails details);
    String sendOtpMail(OtpDto otpDto,EmailDetails details);
}
