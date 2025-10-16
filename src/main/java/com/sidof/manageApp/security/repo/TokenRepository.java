package com.sidof.manageApp.security.repo;

import com.sidof.manageApp.security.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved BIS
 * Since    : 4/14/25
 * </blockquote></pre>
 */


public interface TokenRepository extends JpaRepository<Token,Long> {

    @Query("SELECT t FROM Token t WHERE t.token = ?1")
    Optional<Token> findByToken(String token);

//    @Query("""
//            SELECT t FROM Token t inner join User u ON t.user.id = u.id
//             WHERE u.id = :id AND (t.expired = false OR t.revoked = false)
//            """)

    @Query("""
                SELECT t FROM Token t 
                INNER JOIN User u ON t.user.id = u.id
                WHERE u.id = :id AND (t.expired = false AND t.revoked = false)
            """)
    List<Token> findAllValidTokensByUser(Long id);

    @Query("SELECT t FROM Token t WHERE t.refreshToken = ?1")
    Optional<Token> findByRefreshToken(String refreshToken);
}
