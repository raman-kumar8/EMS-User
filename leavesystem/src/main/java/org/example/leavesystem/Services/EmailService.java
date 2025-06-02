package org.example.leavesystem.Services;

import org.example.leavesystem.Models.SubscriberModel;
import org.example.leavesystem.Repositories.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {
    @Autowired
    private SubscriberRepository subscriberRepository;
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail( String subject,String body)
    {
        List<SubscriberModel> list = subscriberRepository.findAll();
        for(SubscriberModel subscriber : list)
        {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom("saini17102001@gmail.com");
            simpleMailMessage.setTo(subscriber.getEmail());
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body);
            javaMailSender.send(simpleMailMessage);
        }
    }

}
