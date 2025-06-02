package com.sidof.manageApp.handError;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.sidof.manageApp.handError.ErrorCode.ACCOUNT_LOCKED;
import static com.sidof.manageApp.handError.ErrorCode.NO_ACCESS;
import static org.springframework.http.HttpStatus.*;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved BIS
 * Since    : 5/6/25
 * </blockquote></pre>
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.builder().error(NO_ACCESS.getDescription()).errorCode(NO_ACCESS.getCode()).build());
    }

    @ExceptionHandler
    public ResponseEntity<String>handleIllegaleState(IllegalStateException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured"+ e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication Failed: " + ex.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String>exception(Exception e){
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unexpected error "+ e.getMessage());
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleException(LockedException ex) {
        log.error("Error 1 Locked {}", ex.getStackTrace());
        return ResponseEntity.status(UNAUTHORIZED).body(ErrorResponse.builder().error(ex.getMessage()).errorCode(ACCOUNT_LOCKED.getCode()).details(ACCOUNT_LOCKED.getDescription()).build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgumentException(IllegalArgumentException ex) {
        log.error("Error 1 Locked {}", ex.getStackTrace());
        return ResponseEntity.status(NOT_FOUND).body(ErrorResponse.builder().error(ex.getMessage()).errorCode(ErrorCode.NOT_FOUND.getCode()).details(ErrorCode.NOT_FOUND.getDescription()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validError(MethodArgumentNotValidException ex) {
        log.error("Error {}", ex.getStackTrace());
        Set<String> errors = new HashSet<>();
        ex.getBindingResult().getAllErrors().forEach((error)->{
            var message = error.getDefaultMessage();
            errors.add(message);
        });
        return ResponseEntity.status(BAD_REQUEST).body(ErrorResponse.builder().validationError(errors).build());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handNullPointException(NullPointerException ex) {
        return ResponseEntity.status(FORBIDDEN).body("The provide properties is null: " + ex.getLocalizedMessage());
    }



}
