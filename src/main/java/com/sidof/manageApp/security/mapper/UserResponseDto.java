package com.sidof.manageApp.security.mapper;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved SIDOF
 * Since    : 5/14/25
 * </blockquote></pre>
 */

@Builder
@Data
public class UserResponseDto {
    private String email;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private List<String> roles;
    private List<String> permissions;
}
