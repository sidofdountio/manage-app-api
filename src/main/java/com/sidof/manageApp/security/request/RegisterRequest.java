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
 * Version  : V1
 * Licence   : All Right Reserved BIS
 * Since    : 4/28/25
 * </blockquote></pre>
 */

public record RegisterRequest(
        @Email(message = "Email should be in right format")
        @NotBlank
        @Size(max = 50)
        String email,
        @NotEmpty(message = "100")
        @NotBlank
        @Size(min = 3, max = 20)
        String lastName,
        @NotEmpty(message = "101")
        @NotBlank
        @Size(min = 3, max = 20)
        String firstName,
        @NotEmpty(message = "103")
        @NotBlank
        @Size(min = 5, max = 20)
        String idCardNumber,

//        @Size(min = 8)
        @NotBlank
        @Size(min = 6, max = 40)
        String password
) {
}
