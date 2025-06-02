// Java Program to Illustrate Creation Of
// Service implementation class

package com.example.emsuser.service;

import com.example.emsuser.dto.EmailDetails;
import java.io.File;

import com.example.emsuser.dto.OtpDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.example.emsuser.exception.CustomException;
import com.example.emsuser.repository.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

// Annotation
@Service
// Class
// Implementing EmailService interface
public class EmailServiceImpl implements EmailService {

    @Autowired private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}") private String sender;

    // Method 1
    // To send a simple email
    public String sendSimpleMail(EmailDetails details)
    {

        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            // Sending the mail
            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    // Method 2
    // To send an email with attachment
    public String
    sendMailWithAttachment(EmailDetails details)
    {
        // Creating a mime message
        MimeMessage mimeMessage
                = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            // Setting multipart as true for attachments to
            // be send
            mimeMessageHelper
                    = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(
                    details.getSubject());

            // Adding the attachment
            FileSystemResource file
                    = new FileSystemResource(
                    new File(details.getAttachment()));

            mimeMessageHelper.addAttachment(
                    file.getFilename(), file);

            // Sending the mail
            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        }

        // Catch block to handle MessagingException
        catch (MessagingException e) {

            // Display message when exception occurred
            return "Error while sending mail!!!";
        }
    }

    public String sendOtpMail(OtpDto otpDto, EmailDetails details) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlMsg = "<div style=\"font-family: Arial, sans-serif; padding: 20px; border: 1px solid #eee; border-radius: 10px; background-color: #f9f9f9; max-width: 500px; margin: auto;\">" +
                    "<h2 style=\"color: #2c3e50; text-align: center;\">Password Reset OTP</h2>" +
                    "<p style=\"font-size: 16px; color: #333;\">Hello,</p>" +
                    "<p style=\"font-size: 16px; color: #333;\">We received a request to reset your password. Use the OTP below to proceed:</p>" +
                    "<div style=\"text-align: center; margin: 20px 0;\">" +
                    "<span style=\"font-size: 28px; font-weight: bold; color: #e74c3c;\">" + otpDto.getOtp() + "</span>" +
                    "</div>" +
                    "<p style=\"font-size: 14px; color: #555;\">This OTP is valid until <strong>" + otpDto.getExpiryTime() + "</strong>. If you didn't request this, please ignore this email.</p>" +
                    "<p style=\"font-size: 14px; color: #999; text-align: center; margin-top: 30px;\">&copy; 2025 EMS System</p>" +
                    "</div>";

            helper.setText(htmlMsg, true); // true = isHtml
            helper.setTo(details.getRecipient());
            helper.setSubject(details.getSubject()); // FIX: use subject
            helper.setFrom(sender);

            javaMailSender.send(mimeMessage);
            return "OTP mail sent successfully!";
        } catch (MessagingException e) {
            throw new CustomException("Error sending OTP email: " + e.getMessage());
        }
    }


}