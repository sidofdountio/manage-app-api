package com.sidof.manageApp.security.service;

import com.sidof.manageApp.mail.EmailService;
import com.sidof.manageApp.security.config.JwtService;
import com.sidof.manageApp.security.mapper.UserMapper;
import com.sidof.manageApp.security.model.Role;
import com.sidof.manageApp.security.model.Token;
import com.sidof.manageApp.security.model.User;
import com.sidof.manageApp.security.repo.TokenRepository;
import com.sidof.manageApp.security.repo.UserRepository;
import com.sidof.manageApp.security.request.AuthRequest;
import com.sidof.manageApp.security.request.AuthResponse;
import com.sidof.manageApp.security.request.RegisterRequest;
import com.sidof.manageApp.security.request.RegisterResponse;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;

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
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper usersMapper;
    private final EmailService emailService;
    private final RoleService roleService;

    @Value("${application.image}")
    String DEFAULT_USER_IMAGE_PROFILE;

    public UserService(UserRepository userRepository, TokenRepository tokenRepository, JwtService jwtService, AuthenticationManager authenticationManager, UserMapper usersMapper, EmailService emailService, RoleService roleService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.usersMapper = usersMapper;
        this.emailService = emailService;
        this.roleService = roleService;
    }


    public RegisterResponse createNewUser(RegisterRequest request) throws BadRequestException, MessagingException {
        boolean existUser = userRepository.existsByEmail(request.email().trim().toLowerCase());
        if (existUser) {
            log.error("Email already in use. Please different email");
            throw new IllegalStateException("Email already in use. Please different email");
        }
        User newUsers = usersMapper.toUsers(request);

//        find role
        Role role = roleService.getRoleByName("USER");
        newUsers.setRole(role);

//        newUsers.setRole(Role.ADMIN);
        newUsers.setUsername(request.lastName());

        newUsers.setEnable(true);
        log.info("Saving New User Users {}", newUsers);
        User usersSaved = userRepository.save(newUsers);
//          Generate token.
        String activationCode = generateActivationCode(usersSaved);

//        Send email to Users with verification email.
        emailService.sendVerificationEmail(newUsers.getEmail(), newUsers.getLastName(), activationCode);
        return RegisterResponse.builder().username(request.lastName()).build();
    }



    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by("id").descending());
    }

    public User GetUser(Long userId) {
        log.info("Fetching user by userId");
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public String changeUserRole(Role role, Long userId) {
        User userNotFound = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Role roleByName = roleService.getRoleByName(role.getName());
        userNotFound.setRole(roleByName);
        return "role added";
    }


    public AuthResponse authenticate(AuthRequest request) throws BadRequestException {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        var users = userRepository.findByEmail(request.email()).orElseThrow(() -> new BadCredentialsException("Users with account " + request.email() + " not exist."));
        var tokenGenerated = jwtService.generateToken(users);
        //        revoke the previous token
        revokeToken(users);
//        Generate and save new token.
        saveNewToken(users, tokenGenerated);
        return AuthResponse.builder().token(tokenGenerated).username(users.getEmail()).build();
    }


    @Transactional
    public String activateAccount(String token) throws MessagingException {
        var savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));
//        Check if the expiration date is passed
        if (LocalDateTime.now().isAfter(savedToken.getExpireAt())) {
            String reActivationCode = generateActivationCode(savedToken.getUser());
//        Send email to Users with verification email.
            emailService.sendVerificationEmail(savedToken.getUser().getEmail(), savedToken.getUser().getFullName(), reActivationCode);
            throw new RuntimeException("Token is expired. A new token has been sent to email address");
        }
        var userById = userRepository.findById(savedToken.getUser().getId()).orElseThrow(() -> new UsernameNotFoundException("Users not found"));
        log.info("Fetching users {} by ID {}", userById, userById.getId());
        userById.setEnable(true);
        userRepository.save(userById);
//        Revoke token.
        revokeToken(userById);
        return "Account activated";
    }


    public String generateActivationCode(User users) {
        String generatedToken = generatedToken(6);
        var token = Token.builder()
                .token(generatedToken)
                .refreshToken(null)
                .user(users)
                .createAt(LocalDateTime.now())
                .expireAt(LocalDateTime.now().plusMinutes(15))
                .revoked(false)
                .expired(false)
                .build();
//        log.info("saving token {}", token);
        tokenRepository.save(token);
        return generatedToken;
    }


    private void saveNewToken(User users, String token) {
        var tokeToSave = Token
                .builder()
                .user(users)
                .expired(false)
                .revoked(false)
                .token(token)
                .createAt(now())
                .build();
        log.info(" save users token after login,logout or update: {}", token);
        tokenRepository.save(tokeToSave);
    }


    public void revokeToken(User users) {
        var validUserToken = tokenRepository.findAllValidTokensByUser(users.getId());
        if (validUserToken.isEmpty())
            return;
        validUserToken.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        log.info("users token had been revoked while login or logout : ");
        tokenRepository.saveAll(validUserToken);
    }


    private String generatedToken(int length) {
        String characters = "0123456789";
        StringBuilder activationCode = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            activationCode.append(characters.charAt(random.nextInt(characters.length())));
        }
        return activationCode.toString();
    }


    @Transactional
    private void validationAccount(String email) {
        User userByUsername = findUserByUsername(email);
        userByUsername.setEnable(true);
        revokeToken(userByUsername);
    }

    private User findUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with that email not found"));
    }


}
