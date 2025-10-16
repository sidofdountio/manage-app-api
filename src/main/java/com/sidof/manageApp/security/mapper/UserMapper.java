package com.sidof.manageApp.security.mapper;

import com.sidof.manageApp.security.model.User;
import com.sidof.manageApp.security.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved BIS
 * Since    : 4/28/25
 * </blockquote></pre>
 */

@Service
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toUsers(RegisterRequest request) {
        return  new User(request.email(),request.lastName(),request.firstName(), request.idCardNumber(), passwordEncoder.encode(request.password()));
    }
}
