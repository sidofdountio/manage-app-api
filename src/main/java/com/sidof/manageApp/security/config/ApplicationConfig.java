package com.sidof.manageApp.security.config;

import com.sidof.manageApp.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;

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

@Configuration
public class ApplicationConfig {

    //    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    public ApplicationConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider dauAuthenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        int STRENGTH = 12;
        return new BCryptPasswordEncoder(STRENGTH);
    }



    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:30009",
                "http://localhost:9000"));
        configuration.setAllowedHeaders(Arrays.asList(
                ACCEPT,
                AUTHORIZATION,
                CONTENT_TYPE,
                ORIGIN, "Jwt-Token",
                ACCESS_CONTROL_ALLOW_ORIGIN,
                ACCESS_CONTROL_REQUEST_METHOD,
                ACCESS_CONTROL_ALLOW_HEADERS,
                ACCESS_CONTROL_REQUEST_HEADERS, "X-Requested-With", "Origin, Accept"
        ));
        configuration.setExposedHeaders(Arrays.asList(ACCEPT,
                AUTHORIZATION,
                CONTENT_TYPE,
                ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN,
                ACCESS_CONTROL_ALLOW_CREDENTIALS,
                "Jwt-Token", "Filename"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
