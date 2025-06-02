package org.example.leavesystem.Controllers;

import org.example.leavesystem.DTO.SubscribeDTO;
import org.example.leavesystem.Models.SubscriberModel;
import org.example.leavesystem.Repositories.SubscriberRepository;
import org.example.leavesystem.Services.EmailService;
import org.example.leavesystem.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/newsletter")
public class newsletterController {
    private SubscriberRepository subscriberRepository;
    private EmailService emailService;

    @Autowired
    public newsletterController(SubscriberRepository subscriberRepository,EmailService emailService)
    {
        this.emailService = emailService;
        this.subscriberRepository = subscriberRepository;
    }
    @PostMapping("/subscribe")
    public String add(@RequestBody SubscribeDTO subscribeDTO)
    {
        if(subscriberRepository.existsByEmail(subscribeDTO.getEmail()))
        {
            throw new CustomException("Already Subscribed with this email ");
        }
        SubscriberModel subscriberModel = new SubscriberModel();
        subscriberModel.setEmail(subscribeDTO.getEmail());
        subscriberRepository.save(subscriberModel);
        return "Subscribed Successfully !!!";
    }

    @PostMapping("/send")
    public String send(@RequestParam String subject, @RequestParam String message)
    {
        emailService.sendEmail(subject,message);
        return "Email Sent Successfully !!!";
    }

}
