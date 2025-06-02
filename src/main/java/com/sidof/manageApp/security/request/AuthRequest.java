package com.sidof.manageApp.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved SIDOF
 * Since    : 4/28/25
 * </blockquote></pre>
 */

public record AuthRequest(
        @NotEmpty(message = "Email cannot be empty")
        @NotBlank(message = "Email cannot be null")
        @Email(message = "Not a valid email.")
        String email,
        @NotEmpty(message = "Password cannot be empty")
        @Size(min = 8, message = "Password should be 8 character long minimum")
        @NotBlank
        String password
) {


}