package com.example.emsuser.service;

import com.example.emsuser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {
    @Autowired
    private UserRepository userrepository;
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail( String email,String url)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("saini17102001@gmail.com");
        message.setTo(email);
        message.setSubject("Email Verification");
        message.setText("Please click on the following link to verify your account:"+url);
        javaMailSender.send(message);
    }

}
