package com.example.SpringSecurity.PostgreSQL.repository;

import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findByPasswordResetToken(String token);
    Optional<User> findByNewEmailToken(String token);
}
