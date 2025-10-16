package com.sidof.manageApp.security.service;

import com.sidof.manageApp.mail.EmailService;
import com.sidof.manageApp.security.config.JwtService;
import com.sidof.manageApp.security.dto.EditUserRequest;
import com.sidof.manageApp.security.mapper.UserMapper;
import com.sidof.manageApp.security.model.PasswordResetToken;
import com.sidof.manageApp.security.model.Role;
import com.sidof.manageApp.security.model.User;
import com.sidof.manageApp.security.repo.PasswordResetTokenRepository;
import com.sidof.manageApp.security.repo.TokenRepository;
import com.sidof.manageApp.security.repo.UserRepository;
import com.sidof.manageApp.security.request.AuthRequest;
import com.sidof.manageApp.security.request.AuthResponse;
import com.sidof.manageApp.security.request.RegisterRequest;
import jakarta.mail.MessagingException;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserMapper usersMapper;
    @Mock
    private EmailService emailService;
    @Mock
    private RoleService roleService;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Authentication authentication;
    @Captor
    ArgumentCaptor<String> tokenCaptor;


    @InjectMocks
    private UserService underTest;


    @BeforeEach
    void before() {

    }

    @Test
    void shouldAuthenticateUserSuccessfully() throws BadRequestException, MessagingException {
//        Given
        AuthRequest request = new AuthRequest("john@example.com", "password");
        User user = new User();
        user.setEmail("john@example.com");
        user.setLastName("Doe");

//        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
//        SecurityContextHolder.setContext(securityContext);
//        given(securityContext.getAuthentication()).willReturn(authentication);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
//        when(user.isUsingMfa()).thenReturn(false);

//        When
        AuthResponse response = underTest.authenticate(request);
//        Then
        verify(tokenRepository, atLeastOnce()).save(any());
        assertEquals("john@example.com", response.getUsername());
    }


    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        AuthRequest request = new AuthRequest("notfound@example.com", "password123");

        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

//        given(user.isUsingMfa()).willReturn(false);

        // When
        assertThatThrownBy(() ->
                underTest.authenticate(request))
                .hasMessageContaining("Users with account " + request.email() + " not exist.")
                .isInstanceOf(BadCredentialsException.class);
//        Then
        verifyNoInteractions(tokenRepository);

    }


    @Test
    void shouldThrowWhenAuthenticationFails() {
        // Given
        AuthRequest request = new AuthRequest("john@example.com", "wrongpass");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> underTest.authenticate(request));
        verify(userRepository, never()).findByEmail(any());
    }


    @Test
    void shouldRegisterNewUser() throws MessagingException, BadRequestException {
//        Given
        RegisterRequest request = new RegisterRequest("user@gmail.com", "john", "doe", "1234432", "password");
        User user = new User();
        user.setEmail("john@example.com");
        user.setLastName("Doe");

        Role role = new Role();
        role.setName("USER");

//        When
        when(roleService.getRoleByName(anyString())).thenReturn(role);
        when(usersMapper.toUsers(request)).thenReturn(user);
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());

        underTest.createNewUser(request);

//        Then
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());

        User captorValue = argumentCaptor.getValue();
//
        assertThat(captorValue).isEqualTo(user);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() throws MessagingException, BadRequestException {
//        Given
        RegisterRequest request = new RegisterRequest("user@gmail.com", "john", "doe", "1234432", "password");

//        When
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> underTest.createNewUser(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Email already in use. Please different email");
//        Then
        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendVerificationEmail(any(), any(), any());

    }


    @Test
    void shouldUpdateUserProfile() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("Old");
        user.setLastName("Name");

        EditUserRequest request = new EditUserRequest("NewFirst", "NewLast");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        String result = underTest.editUserProfile(userId, request);

        // Then
        assertEquals("User profile updated successfully", result);
        assertEquals("NewFirst", user.getFirstName());
        assertEquals("NewLast", user.getLastName());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrownExceptionWhenUserIdDoesExist() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("Old");
        user.setLastName("Name");

        EditUserRequest request = new EditUserRequest("NewFirst", "NewLast");

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> underTest.editUserProfile(userId, request))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");

        // Then
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldSendEmailWhenUserRequestToChangePassword() throws Exception {
        String email = "john@example.com";
        User user = new User();
        user.setEmail(email);
        user.setLastName("doe");
        user.setFirstName("doe");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//        when(passwordResetTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(passwordResetTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(emailService).setPasswordResetRequest(eq(user.getFirstName()), eq(email), any());

        underTest.requestPasswordReset(email);

        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));

        verify(emailService,never()).sendPasswordResetEmail(eq(email), eq(user.getLastName()), anyString());
    }


//    @Test
//    void shouldCaptureResetToken() throws Exception {
//        String email = "john@example.com";
//        User user = new User();
//        user.setLastName("Doe");
//        user.setEmail(email);
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//        when(passwordResetTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));
//        doNothing().when(emailService).sendPasswordResetEmail(eq(email), eq(user.getLastName()), any());
//
//        underTest.requestPasswordReset(email);
//
//        verify(emailService).sendPasswordResetEmail(eq(email), eq(user.getLastName()), tokenCaptor.capture());
//
//        String capturedToken = tokenCaptor.getValue();
//        System.out.println(capturedToken);
//        assertNotNull(capturedToken);
//        assertTrue(capturedToken.length() > 10); // or pattern check
//    }

    @Test
    void shouldResetPassword() throws Exception {
//        Given

        User user = new User();
        user.setEmail("john@example.com");
        user.setLastName("Doe");
        user.setPassword("oldPassword");

        String token = "db877c8f-91d1-4d56-b5f9-fcf6a61887c2";
        PasswordResetToken expectedToken = new PasswordResetToken();
        expectedToken.setToken(token);
        expectedToken.setExpireAt(LocalDateTime.now().plusMinutes(15));
        expectedToken.setUser(user);
        expectedToken.setUsed(false);

        given(passwordResetTokenRepository.findByToken(any())).willReturn(Optional.of(expectedToken));

//        When
        underTest.resetPassword(token, "newPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(tokenCaptor.capture());


//        Then
        verify(passwordResetTokenRepository).save(any());
        assertNotEquals("oldPassword", userCaptor.getValue().getPassword());

        assertTrue(tokenCaptor.getValue().isUsed());


    }


    @Test
    void editUserProfile() {
    }

    @Test
    void forgotPassword() {
    }

    @Test
    void changeUserRole() {
    }

    @Test
    void activateAccount() {
    }
}