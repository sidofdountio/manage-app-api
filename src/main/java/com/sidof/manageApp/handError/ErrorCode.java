package com.sidof.manageApp.handError;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Getter
public enum ErrorCode {
    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_PASSWORD(300, BAD_REQUEST, "Current password is incorrect"),
    NO_ACCESS(301, UNAUTHORIZED, "Access denied: You don't have permission to access this resource"),
    NEW_PASSWORD_DOES_MATCH(3002, BAD_REQUEST, "The new password does not match"),
    ACCOUNT_LOCKED(3003, FORBIDDEN, "Users account is locked"),
    ACCOUNT_DISABLE(3004, FORBIDDEN, "Users account is disabled"),
    BAD_CREDENTIALS(3005, FORBIDDEN, "Password or email not match"),
    NOT_FOUND(306,FORBIDDEN, "Not found"),
    ;

    private final int code;
    private final String description;
    private final HttpStatus httpStatus;

    ErrorCode(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;
    }
}
