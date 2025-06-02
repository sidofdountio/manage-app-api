package com.sidof.manageApp.security.request;

import lombok.Builder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

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

@Builder
public class RegisterResponse {
    private String username;
    private String imageUrl;
    private LocalDateTime createdAt;

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
