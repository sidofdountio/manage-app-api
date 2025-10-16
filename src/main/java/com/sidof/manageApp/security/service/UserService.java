package com.sidof.manageApp.security.service;

import com.sidof.manageApp.mail.EmailService;
import com.sidof.manageApp.security.config.JwtService;
import com.sidof.manageApp.security.dto.EditUserRequest;
import com.sidof.manageApp.security.mapper.UserMapper;
import com.sidof.manageApp.security.model.*;
import com.sidof.manageApp.security.repo.OtpTokenRepository;
import com.sidof.manageApp.security.repo.PasswordResetTokenRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpTokenRepository otpTokenRepository;

    @Value("${application.image}")
    String DEFAULT_USER_IMAGE_PROFILE;

    public UserService(UserRepository userRepository, TokenRepository tokenRepository, JwtService jwtService, AuthenticationManager authenticationManager, UserMapper usersMapper, EmailService emailService, RoleService roleService, PasswordResetTokenRepository passwordResetTokenRepository, PasswordEncoder passwordEncoder, OtpTokenRepository otpTokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.usersMapper = usersMapper;
        this.emailService = emailService;
        this.roleService = roleService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpTokenRepository = otpTokenRepository;
    }


    /**
     * Authentication
     *
     * @param request
     * @return
     * @throws BadRequestException
     */
    public AuthResponse authenticate(AuthRequest request) throws BadRequestException, MessagingException {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        var users = userRepository.findByEmail(request.email()).orElseThrow(() -> new BadCredentialsException("Users with account " + request.email() + " not exist."));


        if (users.isUsingMfa()) {
            // Generate OTP and send email
            generateOtp(users);
            // Return a special response indicating OTP is required
            return AuthResponse.builder()
                    .username(users.getEmail())
                    .otpRequired(true)
                    .build();
        }


        // Standard JWT generation for users without MFA
        //        revoke the previous token
        revokeToken(users);

        //        Generate and save new token.
        var tokenGenerated = jwtService.generateToken(users);
        String refreshToken = UUID.randomUUID().toString();  // Optionally use secureRandom()

        saveNewToken(users, tokenGenerated,refreshToken);
        return AuthResponse.builder().token(tokenGenerated).username(users.getEmail()).build();
    }

    //    Register user
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

        newUsers.setUsername(request.email());
        newUsers.setEnable(false);

        log.info("Saving New User Users {}", newUsers);
        User usersSaved = userRepository.save(newUsers);
//          Generate token.
        String activationCode = generateActivationCode(usersSaved);

//        Send email to Users with verification email.
        emailService.sendVerificationEmail(newUsers.getEmail(), newUsers.getLastName(), activationCode);
        return RegisterResponse.builder().username(request.lastName()).build();
    }


    //    edit user
    @Transactional
    public String editUserProfile(Long userId, EditUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        userRepository.save(user);

        return "User profile updated successfully";
    }


    //    find user by username
    private User findUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with that email not found"));
    }



    public User getUser(Long userId) {
        log.info("Fetching user by user Id");
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public String changeUserRole(Role role, Long userId, Authentication authentication) {
        User userNotFound = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Role roleByName = roleService.getRoleByName(role.getName());
        userNotFound.setRole(roleByName);
        return "role added";
    }


    @Transactional
    public String activateAccount(String token) throws MessagingException {
        var savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));

//        Check if the expiration date is passed
        if (now().isAfter(savedToken.getExpireAt())) {
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


    public void requestPasswordReset(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Create token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setUsed(false);
        resetToken.setExpireAt(LocalDateTime.now().plusMinutes(15));

        passwordResetTokenRepository.save(resetToken);

        // Send email (you can include a reset link here)
        emailService.setPasswordResetRequest(
                user.getFirstName(),
                user.getEmail(),
                token
        );
    }



    public String generateActivationCode(User users) {
        String generatedToken = generatedToken(6);
        var token = Token.builder()
                .token(generatedToken)
                .refreshToken(null)
                .user(users)
                .createAt(now())
                .expireAt(now().plusMinutes(15))
                .revoked(false)
                .expired(false)
                .build();
//        log.info("saving token {}", token);
        tokenRepository.save(token);
        return generatedToken;
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

    private void saveNewToken(User users, String token, String refreshToken) {
        var tokeToSave = Token
                .builder()
                .user(users)
                .expired(false)
                .revoked(false)
                .token(token)
                .refreshToken(refreshToken)
                .expireAt(LocalDateTime.now().plusDays(7))
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


    /**
     * Verifies refresh token and returns a new access token.
     * @param refreshToken
     */
    public AuthResponse refreshToken(String refreshToken) {
        // 1. Find token by refresh value
        Token savedToken = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // 2. Validate expiration and revocation
        if (savedToken.isExpired() || savedToken.isRevoked()) {
            throw new RuntimeException("Refresh token expired or revoked");
        }

        User user = savedToken.getUser();

        //3, 🔁 Revoke all valid tokens for this user
        revokeToken(user);

        // 4. Generate a new access token
        String newAccessToken = jwtService.generateToken(user);

        // 5. Save new access token to DB (optional: or just update existing record)
        savedToken.setToken(newAccessToken);
        savedToken.setCreateAt(LocalDateTime.now());
        savedToken.setExpireAt(LocalDateTime.now().plusMinutes(15)); // Match your JWT expiry config
        tokenRepository.save(savedToken);

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(savedToken.getRefreshToken())
                .username(user.getEmail())
                .build();
    }

    /**
     * Reset password
     *
     * @param token
     * @param newPassword
     */
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpireAt().isBefore(now()) || resetToken.isUsed())
            throw new RuntimeException("Token is expired or already used");

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // Optionally revoke tokens
        revokeToken(user);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by("id").descending());
    }



    @Transactional
    private void validationAccount(String email) {
        User userByUsername = findUserByUsername(email);
        userByUsername.setEnable(true);
        revokeToken(userByUsername);
    }


    public String generateOtp(User user) throws MessagingException {
        // 1. Generate 6-digit OTP
        String code = String.format("%06d", new SecureRandom().nextInt(999999));

        // 2. Save OTP in DB
        OtpToken otpToken = new OtpToken();
        otpToken.setUser(user);
        otpToken.setCode(code);
        otpToken.setUsed(false);
        otpToken.setCreatedAt(LocalDateTime.now());
        otpToken.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // valid for 5 min
        otpTokenRepository.save(otpToken);

        // 3. Send OTP via email
        emailService.sendOtpEmail(user.getEmail(), user.getFirstName(), code);

        return code;
    }

    public AuthResponse verifyOtp(String otp) {


        // 1. Check if OTP matches and not expired
        OtpToken otpToken = otpTokenRepository.findByCode(otp)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        // 2. Check if OTP matches and not expired
        if (!otpToken.getCode().equals(otp) || otpToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        User user = userRepository.findById(otpToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        // 4. Mark user as verified
        user.setMfaVerified(true);
        userRepository.save(user);

        // 5. Revoke OTP
        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);

        // 6. Generate new JWT token
        // Generate new JWT + refresh token
        String token = jwtService.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();
        saveNewToken(user, token, refreshToken);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .username(user.getEmail())
                .build();
    }



    @Transactional
    public void enableMfa(String email) {
        // 1️⃣ Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 2️⃣ Check if already enabled
        if (user.isUsingMfa()) {
            throw new IllegalStateException("MFA is already enabled for this account");
        }

        // 3️⃣ Enable MFA
        user.setUsingMfa(true);
        user.setMfaVerified(false); // ensure verification is reset
        userRepository.save(user);
    }


}
