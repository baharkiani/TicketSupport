package com.example.TicketSupport.config;

import com.example.TicketSupport.filter.JwtAuthenticationFilter;
import com.example.TicketSupport.security.CachedBodyFilter;
import com.example.TicketSupport.security.CustomAuthorizationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CachedBodyFilter cachedBodyFilter;
    private final CustomAuthorizationManager customAuthorizationManager;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CachedBodyFilter cachedBodyFilter,
            CustomAuthorizationManager customAuthorizationManager
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.cachedBodyFilter = cachedBodyFilter;
        this.customAuthorizationManager = customAuthorizationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /*
                 * CachedBodyFilter
                 *
                 * قبل از JwtAuthenticationFilter اجرا می‌شود.
                 */
                .addFilterBefore(
                        cachedBodyFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                /*
                 * JwtAuthenticationFilter
                 *
                 * بعد از CachedBodyFilter و قبل از
                 * UsernamePasswordAuthenticationFilter اجرا می‌شود.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/users/login",
                                "/api/users/register",
                                "/api/users/refresh"
                        )
                        .permitAll()

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        )
                        .permitAll()

                        .anyRequest()
                        .access(customAuthorizationManager)
                );

        return http.build();
    }
}