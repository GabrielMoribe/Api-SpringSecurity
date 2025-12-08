package com.example.SpringSecurity.PostgreSQL.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id" , referencedColumnName = "id")
    private User user;

    @Column(name = "token")
    private String refreshToken;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
