package com.sidof.manageApp.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Since    : 4/14/25
 * </blockquote></pre>
 */
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

@Entity
@Table(
        name = "token",
        uniqueConstraints = @UniqueConstraint(columnNames = "token", name = "UQ_Token_token")
)
public class Token {
    @Id
    @SequenceGenerator(name = "token_sequence_id",allocationSize = 1,initialValue = 1)
    @GeneratedValue(generator = "token_sequence_id",strategy = GenerationType.SEQUENCE)
    private Long id;
    private String token;
    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_token_user"))
    private User user;


    private LocalDateTime createAt;

    private LocalDateTime expireAt;

    private boolean expired;
    private boolean revoked;



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}
