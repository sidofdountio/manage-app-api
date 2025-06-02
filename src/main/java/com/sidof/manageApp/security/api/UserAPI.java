package com.sidof.manageApp.security.api;

import com.sidof.manageApp.security.mapper.UserResponseDto;
import com.sidof.manageApp.security.model.User;
import com.sidof.manageApp.security.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved SIDOF
 * Since    : 5/6/25
 * </blockquote></pre>
 */

@RestController
@RequestMapping("/user")
public class UserAPI {

    private final UserService userService;

    public UserAPI(UserService userService) {
        this.userService = userService;
    }


//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() throws AccessDeniedException {
        List<User> USERS = userService.getAllUsers();
        return new ResponseEntity<>(USERS, OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal(); // your User implements UserDetails

        List<String> authorities = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        List<String> roles = authorities.stream()
                .filter(auth -> auth.startsWith("ROLE_"))
                .map(auth -> auth.replace("ROLE_", ""))
                .toList();

        List<String> permissions = authorities.stream()
                .filter(auth -> !auth.startsWith("ROLE_"))
                .toList();

        return new ResponseEntity<>(UserResponseDto.builder()
                .email(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .imageUrl(user.getImageUrl())
                .roles(roles)
                .permissions(permissions)
                .build(), OK);
    }
}
