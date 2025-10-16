package com.sidof.manageApp.security.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved BIS
 * Since    : 6/30/25
 * </blockquote></pre>
 */

@Entity
public class PasswordResetToken {
    @Id
    @GeneratedValue
    private Long id;
    private String token;
    private LocalDateTime expireAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private boolean used = false;

    public PasswordResetToken() {
    }

    public PasswordResetToken(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
