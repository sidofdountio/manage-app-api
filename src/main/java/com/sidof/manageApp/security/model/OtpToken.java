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
 * Since    : 8/23/25
 * </blockquote></pre>
 */

@Entity
@Table(
        name = "otp_tokens",
        indexes = {
                @Index(name = "idx_otp_user_used_created", columnList = "user_id, used, created_at DESC"),
                @Index(name = "idx_otp_user_code", columnList = "user_id, code")
        }
)
public class OtpToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // good fit for MySQL
    private Long id;

    @Column(nullable = false, length = 6)
    private String code; // 6-digit OTP

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(name = "fk_otp_user")
    )
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // getters/setters (or Lombok @Data / @Getter @Setter)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

}
