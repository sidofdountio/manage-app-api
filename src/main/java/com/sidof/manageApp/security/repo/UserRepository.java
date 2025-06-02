package com.sidof.manageApp.security.repo;

import com.sidof.manageApp.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1
 * Licence   : All Right Reserved SIDOF
 * Since    : 4/13/25
 * </blockquote></pre>
 */

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
}
