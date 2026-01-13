package com.example.SpringSecurity.PostgreSQL.integrationTests.repository;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.SpringSecurity.PostgreSQL.domain.entity.RefreshToken;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.domain.enums.Roles;
import com.example.SpringSecurity.PostgreSQL.repository.RefreshTokenRepository;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class RefreshTokenRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    public void setup() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("user");
        user.setEmail("user@email.com");
        user.setPassword("123123");
        user.setEnabled(true);
        user.setRole(Roles.USER);
        userRepository.save(user);

        refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setRefreshToken(generateToken(user));
        refreshToken.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        refreshTokenRepository.save(refreshToken);

    }

    public String generateToken(User user){
        Algorithm algorithm = Algorithm.HMAC256("secret-key");
        return JWT.create()
                .withClaim("userId" , user.getId())
                .withSubject(user.getEmail())
                .withClaim("role" ,user.getRole().name())
                .withExpiresAt(Instant.now().plusSeconds(600))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    @Nested
    @DisplayName("Metodo findByRefreshToken")
    class FindByRefreshToken{
        @Test
        @DisplayName("Deve retornar o RefreshToken quando o token existir")
        void shouldReturnRefreshTokenWhenTokenExists() {
            Optional<RefreshToken> token = refreshTokenRepository.findByRefreshToken(refreshToken.getRefreshToken());
            assertThat(token).isPresent();
        }
        @Test
        @DisplayName("Deve retornar vazio quando o token nao existir")
        void shouldReturnEmptyIfTokenDoesNotExist() {
            Optional<RefreshToken> token = refreshTokenRepository.findByRefreshToken("invalid-token");
            assertThat(token).isNotPresent();
        }
    }


    @Nested
    @DisplayName("Metodo deleteByRefreshToken")
    class DeleteByRefreshToken{
        @Test
        @DisplayName("Deve deletar o RefreshToken por token")
        void shouldDeleteRefreshTokenWhenTokenExists() {
            refreshTokenRepository.deleteByRefreshToken(refreshToken.getRefreshToken());
            Optional<RefreshToken> token = refreshTokenRepository.findByRefreshToken(refreshToken.getRefreshToken());
            assertThat(token).isNotPresent();
        }
    }


    @Nested
    @DisplayName("Metodo deleteByUser")
    class DeleteByUser{
        @Test
        @DisplayName("Deve deletar o RefreshToken por usuario")
        void shouldDeleteRefreshTokenWhenUserExists() {
            refreshTokenRepository.deleteByUser(user);
            Optional<RefreshToken> token = refreshTokenRepository.findByRefreshToken(refreshToken.getRefreshToken());
            assertThat(token).isNotPresent();
        }
    }
}
