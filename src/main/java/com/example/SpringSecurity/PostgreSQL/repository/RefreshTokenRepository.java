package com.example.SpringSecurity.PostgreSQL.repository;

import com.example.SpringSecurity.PostgreSQL.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String token);
    void deleteByRefreshToken(String token);
}
