package com.example.SpringSecurity.PostgreSQL.integrationTests.repository;


import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private UserRepository userRepository;


    @Test
    void databaseIsConnected(){
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void setUp(){
        User user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@email.com");
        user1.setPassword("123123");
        user1.setEnabled(false);

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@email.com");
        user2.setPassword("123123");
        user2.setEnabled(true);

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    void shouldReturnUserByEmail(){
        Optional<User> user = userRepository.findUserByEmail("user1@email.com");
        assertThat(user).isNotNull();
    }

}
