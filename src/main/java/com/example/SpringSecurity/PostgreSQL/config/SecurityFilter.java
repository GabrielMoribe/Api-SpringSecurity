package com.example.SpringSecurity.PostgreSQL.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private TokenConfig tokenConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(Strings.isNotEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            Optional<JWTUserData> optUser = tokenConfig.validateToken(token);
            if(optUser.isPresent()) {
                JWTUserData userData = optUser.get();
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userData , null , null);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request , response);
        }
        else{
            filterChain.doFilter(request , response);
        }
    }
}
