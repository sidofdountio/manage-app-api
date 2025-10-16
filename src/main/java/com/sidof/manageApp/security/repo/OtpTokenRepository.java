package com.sidof.manageApp.security.repo;

import com.sidof.manageApp.security.model.OtpToken;
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
 * Licence   : All Right Reserved BIS
 * Since    : 8/23/25
 * </blockquote></pre>
 */

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {


    // Optionally find by code and user
    Optional<OtpToken> findByUserAndCodeAndUsedFalse(User user, String code);

    Optional<OtpToken> findByUserId(Long id);

    Optional<OtpToken> findByCode(String code);
}
