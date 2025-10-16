package com.sidof.manageApp.security.api;

import com.sidof.manageApp.project.response.CustomResponse;
import com.sidof.manageApp.security.request.AuthRequest;
import com.sidof.manageApp.security.request.AuthResponse;
import com.sidof.manageApp.security.request.RefreshTokenRequest;
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
public class AuthAPI {
    private final UserService userService;


    public AuthAPI(UserService userService) {
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
    public ResponseEntity<String> loginTest(@RequestBody @Valid AuthRequest request) throws BadRequestException, MessagingException {
        AuthResponse authenticate = userService.authenticate(request);
        return new ResponseEntity<String>(authenticate.getToken(), OK);
    }


    @PostMapping("/authentication")
    public ResponseEntity<CustomResponse> login(@RequestBody @Valid AuthRequest request) throws BadRequestException, MessagingException {
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

    @GetMapping("/request-password-reset")
    public ResponseEntity<CustomResponse> requestPasswordReset(@RequestParam String email) throws MessagingException {
        userService.requestPasswordReset(email);
        return new ResponseEntity<>(CustomResponse.builder()
                .data(Map.of("account", "request-password-reset"))
                .status(ACCEPTED)
                .message("Request to change password send")
                .statusCode(ACCEPTED.value())
                .timeStamp(LocalDateTime.now())
                .build(), ACCEPTED);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<CustomResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return new ResponseEntity<>(CustomResponse.builder()
                .data(Map.of("account", userService.refreshToken(request.refreshToken())))
                .status(ACCEPTED)
                .message("Account successfully activated")
                .statusCode(ACCEPTED.value())
                .timeStamp(LocalDateTime.now())
                .build(), ACCEPTED);
    }


    @GetMapping("/enable-mfa")
    public ResponseEntity<CustomResponse> enableMfa(@RequestParam String email) {
        userService.enableMfa(email);
        return new ResponseEntity<>(CustomResponse.builder()
                .data(Map.of("account", "MFA setup initiated. Check your email for OTP."))
                .status(ACCEPTED)
                .message("MFA setup initiated. Check your email for OTP.")
                .statusCode(ACCEPTED.value())
                .timeStamp(LocalDateTime.now())
                .build(), ACCEPTED);
    }


    @GetMapping("/verify-otp")
    public ResponseEntity<CustomResponse> verifyOtp(@RequestParam String code) {
        return new ResponseEntity<>(CustomResponse.builder()
                .data(Map.of("account", userService.verifyOtp(code)))
                .status(ACCEPTED)
                .message("OTP verified successfully")
                .statusCode(ACCEPTED.value())
                .timeStamp(LocalDateTime.now())
                .build(), ACCEPTED);
    }


}
