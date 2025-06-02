package com.sidof.manageApp.security.repo;

import com.sidof.manageApp.security.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DataJpaTest
class UserRepositoryTest {
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
        userRepository.delete(user);
    }

    @Test
    void shouldFindByEmail() {
        Optional<User> userByEmail = userRepository.findByEmail(user.getEmail());
        assertTrue(userByEmail.isPresent());
    }
    @Test
    void shouldReturnEmptyWhenFindByEmailDoesExist() {
        User noExistUser = new User();
        Optional<User> userByEmail = userRepository.findByEmail(noExistUser.getEmail());
        assertTrue(userByEmail.isEmpty());
//        assertNull(userByEmail.get());
    }

    @Test
    void existsByEmail() {
    }

    @Test
    void findByUsername() {
    }
}