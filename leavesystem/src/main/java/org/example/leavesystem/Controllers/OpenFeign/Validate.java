package org.example.leavesystem.Controllers.OpenFeign;

import org.example.leavesystem.DTO.UpdateLeaveCountDTO;
import org.example.leavesystem.DTO.UserDetailsResponseDTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * OpenFeign client interface for validating JWT tokens.
 * This interface communicates with the authentication service to validate tokens
 * and retrieve user information.
 */
@FeignClient(name = "validate", url = "${USER_URL}")
public interface Validate {

    /**
     * Validates a JWT token and returns the user ID if valid.
     *
     * @param cookieHeader The HTTP Cookie header containing the JWT token
     * @return The user ID as a string if the token is valid
     * @throws org.springframework.web.client.HttpClientErrorException if the token is invalid
     */
    @RequestMapping(method = RequestMethod.GET, value = "/general/validate")
    String validate(@RequestHeader("Cookie") String cookieHeader);

    @RequestMapping(method=RequestMethod.GET,value = "/general/user")
    ResponseEntity<UserDetailsResponseDTo> getUserDetails(@CookieValue("jwt_token") String token);

    @RequestMapping(method=RequestMethod.GET,value = "/general/user1")
    ResponseEntity<UserDetailsResponseDTo> getUserByID(@RequestParam("id") UUID id);
    @RequestMapping(method=RequestMethod.POST,value = "/general/setLeaveCount")
    void setLeaveCount(@RequestBody  UpdateLeaveCountDTO updateLeaveCountDTO);

}
