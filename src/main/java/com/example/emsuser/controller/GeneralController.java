package com.example.emsuser.controller;

import com.example.emsuser.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/general")
public class GeneralController {
   @Autowired
   private JwtTokenProvider jwtTokenProvider;
    @GetMapping("/validate")
    public String validate(@CookieValue("jwt_token") String token) {
        UUID userId = jwtTokenProvider.getUserIdFromJWT(token);
         return  userId.toString();

    }
}
