package com.sidof.manageApp.security.repo;

import com.sidof.manageApp.security.model.Token;
import com.sidof.manageApp.security.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TokenRepositoryTest {
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;



    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                "test@example.com",
                "securePassword",
                "Test",
                "User"
        );
        user = userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldFindTokenByTokenString() {
        Token token = Token.builder()
                .token("abc123")
                .user(user)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
        Optional<Token> result = tokenRepository.findByToken("abc123");

        assertTrue(result.isPresent());
        assertEquals("abc123", result.get().getToken());
    }

    @Test
    void shouldFindAllValidTokensByUser() {
        Token token = Token.builder()
                .token("abc123")
                .user(user)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);

        List<Token> allValidTokensByUser = tokenRepository.findAllValidTokensByUser(user.getId());

        allValidTokensByUser.forEach(token1 -> {
            assertFalse(token1.isExpired());
            assertFalse(token1.isRevoked());
        });

    }


    @Test
    void shouldReturnEmptyWhenTokenNotFound() {
        Optional<Token> result = tokenRepository.findByToken("nonexistent-token");

        assertTrue(result.isEmpty(), "Expected empty result for nonexistent token");
    }

    @Test
    void shouldTrowExceptionWhenUserTokenIsInvalid() {
        Token token = Token.builder()
                .token("abc123")
                .user(user)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);


    }
}