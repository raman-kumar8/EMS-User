package com.example.emsuser.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendHtmlEmail(String recipientEmail, String verificationUrl) {
        try {

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("saini17102001@gmail.com");
            helper.setTo(recipientEmail);
            helper.setSubject("Verify Your EMS Account");

            String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
                    <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
                        <h2 style="color: #2c3e50;">Welcome to EMS!</h2>
                        <p style="font-size: 16px; color: #555;">
                            Thank you for registering with the Employee Management System.
                            Please verify your email by clicking the button below:
                        </p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #3498db; color: white; padding: 12px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;">
                                Verify Email
                            </a>
                        </div>
                        <p style="font-size: 14px; color: #888;">
                            If you did not request this, please ignore this email.
                        </p>
                        <p style="font-size: 14px; color: #888;">
                            Regards,<br>
                            EMS Team
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(verificationUrl);

            helper.setText(htmlContent, true);  // true = isHtml
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();  // Or log the error
        }
    }
}
