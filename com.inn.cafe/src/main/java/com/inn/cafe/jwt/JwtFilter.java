package com.inn.cafe.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomerUserDetailsService customerUserDetailsService;

    private Claims claims = null;
    private String userName = null;

    public JwtFilter(JwtUtil jwtUtil, CustomerUserDetailsService customerUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customerUserDetailsService = customerUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")) {
            filterChain.doFilter(request, response);
        } else {
            var authHeader = request.getHeader("Authorization");
            var token = "";

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                userName = jwtUtil.extractUsername(token);
                claims = jwtUtil.extractAllClaims(token);
            }

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = this.customerUserDetailsService.loadUserByUsername(userName);

                if (jwtUtil.validateToken(token, userDetails)) {
                    var userNamePasswordAuthToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    userNamePasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(userNamePasswordAuthToken);
                }
            }

            filterChain.doFilter(request, response);
        }
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase((String) this.claims.get("role"));
    }

    public boolean isUser() {
        return "user".equalsIgnoreCase((String) this.claims.get("role"));
    }

    public String getUserName() {
        return userName;
    }
}
