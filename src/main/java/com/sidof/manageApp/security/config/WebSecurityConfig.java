package com.sidof.manageApp.security.config;

import com.sidof.manageApp.security.service.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * <blockquote><pre>
 * Author   : @Dountio
 * LinkedIn : @SidofDountio
 * GitHub   : @SidofDountio
 * Version  : V1.0
 * Email    : sidofdountio406@gmail.com
 * Licence  : All Right Reserved SIDOF
 * Since    : 4/14/25
 * </blockquote></pre>
 */


@Configuration
//@EnableWebSecurity
@EnableMethodSecurity// annotation
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutService logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(
                        req -> req
                                .requestMatchers("/auth/**", "/public/**").permitAll()
                                .requestMatchers("/actuator/**").permitAll()

                                .requestMatchers("/user/users/**").hasRole("ADMIN")


//                                .requestMatchers("/user/me/**").hasRole("USER")
//                                .requestMatchers("/user/me/**").hasRole("ADMIN")


                                .requestMatchers("/test/user/**").hasRole("USER")
                                .requestMatchers("/test/admin/**").hasRole("ADMIN")
                                .requestMatchers("/test/manager/**").hasRole("MANAGER")
                                .requestMatchers("/test/system/**").hasRole("SYSTEM")

                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(
                        (logout) -> logout
                                .logoutUrl("/api/manage-sale/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler(((request, response, authentication) ->
                                        SecurityContextHolder.clearContext()))
                                .permitAll());

        return http.build();
    }
}
