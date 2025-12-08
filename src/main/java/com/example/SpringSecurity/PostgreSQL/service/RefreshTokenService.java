package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.domain.entity.RefreshToken;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.repository.RefreshTokenRepository;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String email) {
        Optional<User> userOpt = userRepository.findUserByEmail(email);
        if(userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuario nao encontrado");
        }
        else{
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(userOpt.get());
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiresAt(LocalDateTime.now().plusDays(10));
            return refreshTokenRepository.save(refreshToken);
        }
    }


    public RefreshToken verifyToken(String refreshToken) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByRefreshToken(refreshToken);
        if(refreshTokenOpt.isEmpty()) {
            throw new RuntimeException("token nao encontrado"); //criar excecao
        }
        if(refreshTokenOpt.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteByRefreshToken(refreshTokenOpt.get().getRefreshToken());
            throw new RuntimeException("token expirado");
        }
        return refreshTokenOpt.get();
    }
}
