package com.inn.cafe.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomerUserDetailsService customerUserDetailsService;

    public SecurityConfig(CustomerUserDetailsService customerUserDetailsService) {
        this.customerUserDetailsService = customerUserDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(this.customerUserDetailsService)
                .passwordEncoder(this.passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors()
                .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()
                .csrf()
                .disable()
                .authorizeHttpRequests((auth) ->
                        auth.anyRequest().authenticated()
                                .requestMatchers("/user/login", "/user/signup", "/user/forgottenPassword")
                                .anonymous()
                                .and())
                .exceptionHandling()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().build();

    }
}
