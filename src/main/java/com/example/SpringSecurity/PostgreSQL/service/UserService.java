package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.config.JWTUserData;
import com.example.SpringSecurity.PostgreSQL.domain.dto.request.UpdateUserRequest;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JWTUserData userData) {
            return userRepository.findById(userData.userId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("User not authenticated");
    }

    @Transactional
    public User updateUser(UpdateUserRequest request) {
        User user = getAuthenticatedUser();

        if (request.name() != null && !request.name().isEmpty()) {
            user.setName(request.name());
        }
        if (request.email() != null && !request.email().isEmpty()) {
            user.setEmail(request.email());
        }
        return userRepository.save(user);
    }

    public void deleteUser() {
        User user = getAuthenticatedUser();
        userRepository.delete(user);
    }

    public User findUser() {
        return getAuthenticatedUser();
    }
}
