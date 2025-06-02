package com.sidof.manageApp.security.api;

import com.sidof.manageApp.project.response.CustomResponse;
import com.sidof.manageApp.security.request.AuthRequest;
import com.sidof.manageApp.security.request.AuthResponse;
import com.sidof.manageApp.security.request.RegisterRequest;
import com.sidof.manageApp.security.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved SIDOF
 * Since    : 4/28/25
 * </blockquote></pre>
 */

@RestController
@RequestMapping("/auth")
public class Auth {
    private final UserService userService;


    public Auth(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    @ResponseStatus(CREATED)
    public ResponseEntity<CustomResponse> register(@RequestBody @Valid RegisterRequest request) throws BadRequestException, MessagingException {
        return new ResponseEntity<>(
                CustomResponse.builder()
                        .data(Map.of("register", userService.createNewUser(request)))
                        .status(CREATED)
                        .message("Account created. Check your email to enable your account")
                        .statusCode(CREATED.value())
                        .timeStamp(LocalDateTime.now())
                        .build(), CREATED);
    }

    @PostMapping("/test/authentication")
    public ResponseEntity<String> loginTest(@RequestBody @Valid AuthRequest request) throws  BadRequestException {
        AuthResponse authenticate = userService.authenticate(request);
        return new ResponseEntity<String>(authenticate.getToken(), OK);
    }


    @PostMapping("/authentication")
    public ResponseEntity<CustomResponse> login(@RequestBody @Valid AuthRequest request) throws BadRequestException {
        return new ResponseEntity<>(
                CustomResponse.builder()
                        .data(Map.of("token", userService.authenticate(request)))
                        .status(OK)
                        .message("users logged in")
                        .statusCode(OK.value())
                        .timeStamp(LocalDateTime.now())
                        .build(), OK);
    }

    @GetMapping("/activate-account")
    public ResponseEntity<CustomResponse> activateAccount(@RequestParam String token) throws  MessagingException {
        return new ResponseEntity<>(CustomResponse.builder()
                .data(Map.of("account",userService.activateAccount(token) ))
                .status(ACCEPTED)
                .message("Account successfully activated")
                .statusCode(ACCEPTED.value())
                .timeStamp(LocalDateTime.now())
                .build(), ACCEPTED);
    }
}
