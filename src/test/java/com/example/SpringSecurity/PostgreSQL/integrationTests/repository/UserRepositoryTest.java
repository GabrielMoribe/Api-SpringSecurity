package com.example.SpringSecurity.PostgreSQL.integrationTests.repository;

import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.domain.enums.Roles;
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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private UserRepository userRepository;

    private User user;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = new User();
        user.setName("user");
        user.setEmail("user@email.com");
        user.setPassword("123123");
        user.setEnabled(true);
        user.setRole(Roles.USER);
        user = userRepository.save(user);
    }

    @Nested
    @DisplayName("Metodo findUserByEmail")
    class FindUserByEmail {
        @Test
        @DisplayName("Deve retornar um usuário pelo email, caso exista")
        void shouldReturnUserByEmail(){
            Optional<User> user = userRepository.findUserByEmail("user@email.com");
            assertThat(user).isPresent();
            assertThat(user.get().getName()).isEqualTo("user");
            assertThat(user.get().getEmail()).isEqualTo("user@email.com");
            assertThat(user.get().getRole()).isEqualTo(Roles.USER);
        }
        @Test
        @DisplayName("Deve retornar vazio, caso não exista um usuário com o email informado")
        void shouldReturnEmptyWhenUserNotFoundByEmail(){
            Optional<User> user = userRepository.findUserByEmail("teste@email.com");
            assertThat(user).isNotPresent();
        }
    }


    @Nested
    @DisplayName("Metodo findByPasswordResetToken")
    class  FindUserByPasswordResetToken {
        @Test
        @DisplayName("Deve retornar um usuário pelo token de reset de senha, caso exista")
        void shouldReturnUserByPasswordResetToken() {
            user.setPasswordResetToken("reset-token-123");
            userRepository.save(user);

            Optional<User> userOpt = userRepository.findByPasswordResetToken("reset-token-123");
            assertThat(userOpt).isPresent();
            assertThat(userOpt.get().getEmail()).isEqualTo("user@email.com");
        }
        @Test
        @DisplayName("Deve retornar vazio, caso não exista um usuário com o token de reset de senha informado")
        void shouldReturnEmptyWhenUserNotFoundByPasswordResetToken() {
            Optional<User> userOpt = userRepository.findByPasswordResetToken("non-existent-token");
            assertThat(userOpt).isNotPresent();
        }

    }


    @Nested
    @DisplayName("Metodo findByNewEmailToken")
    class  FindUserByNewEmailToken {
        @Test
        @DisplayName("Deve retornar um usuário pelo token de novo email, caso exista")
        void shouldReturnUserByNewEmailToken() {
            user.setNewEmailToken("new-email-token-123");
            userRepository.save(user);

            Optional<User> userOpt = userRepository.findByNewEmailToken("new-email-token-123");
            assertThat(userOpt).isPresent();
            assertThat(userOpt.get().getEmail()).isEqualTo("user@email.com");
        }
        @Test
        @DisplayName("Deve retornar vazio, caso não exista um usuário com o token de")
        void shouldReturnEmptyWhenUserNotFoundByNewEmailToken() {
            Optional<User> userOpt = userRepository.findByNewEmailToken("non-existent-token");
            assertThat(userOpt).isNotPresent();
        }
    }


    @Nested
    @DisplayName("Metodo findByMpPaymentId")
    class  FindUserByMpPaymentId {
        @Test
        @DisplayName("Deve retornar um usuário pelo mpPaymentId, caso exista")
        void shouldReturnUserByMpPaymentId() {
            user.setMpPaymentId("mp-payment-id-123");
            userRepository.save(user);

            Optional<User> userOpt = userRepository.findByMpPaymentId("mp-payment-id-123");
            assertThat(userOpt).isPresent();
            assertThat(userOpt.get().getEmail()).isEqualTo("user@email.com");
        }
        @Test
        @DisplayName("Deve retornar vazio, caso não exista um usuário com o mpPayment")
        void shouldReturnEmptyWhenUserNotFoundByMpPaymentId() {
            Optional<User> userOpt = userRepository.findByMpPaymentId("non-existent-mp-payment-id");
            assertThat(userOpt).isNotPresent();
        }
    }
}
