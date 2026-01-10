package com.example.SpringSecurity.PostgreSQL.integrationTests.repository;

import com.example.SpringSecurity.PostgreSQL.domain.entity.Client;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.domain.enums.Roles;
import com.example.SpringSecurity.PostgreSQL.repository.ClientRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class ClientRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;

    private User user;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        clientRepository.deleteAll();

        user = new User();
        user.setName("user");
        user.setEmail("user@email.com");
        user.setPassword("123123");
        user.setEnabled(true);
        user.setRole(Roles.USER);
        user = userRepository.save(user);
    }

    public Client createClient(User user , String name , String email){
        Client client = new Client();
        client.setName(name);
        client.setEmail(email);
        client.setPhone("9999-9999");
        client.setBroker(user);
        return clientRepository.save(client);
    }

    @Nested
    @DisplayName("Metodo findByBroker")
    class FindUserByEmail {
        @Test
        @DisplayName("Deve retornar clientes por usuario, caso existam")
        void shouldReturnClientByBroker(){
            createClient(user , "Client 1" , "client1@email.com");
            createClient(user , "Client 2" , "client2@email.com");
            List<Client> client = clientRepository.findByBroker(user);
            assertThat(client.getFirst().getName()).isEqualTo("Client 1");
            assertThat(client).hasSize(2);
        }
        @Test
        @DisplayName("Deve retornar vazio, caso não exista um cliente para o usuario informado")
        void shouldReturnEmptyWhenClientNotFoundByBroker(){
            List<Client> client = clientRepository.findByBroker(user);
            assertThat(client).isEmpty();
        }
    }

    @Nested
    @DisplayName("Metodo findByIdAndBroker")
    class  FindClientByIdAndBroker {
        @Test
        @DisplayName("Deve retornar um cliente pelo id e usuario, caso exista")
        void shouldReturnClientByIdAndBroker() {
            Client newClient = createClient(user , "Client 1" , "client1@email.com");
            Optional<Client> client = clientRepository.findByIdAndBroker(newClient.getId(), user);
            assertThat(client).isPresent();
            assertThat(client.get().getName()).isEqualTo("Client 1");
            }
        @Test
        @DisplayName("Deve retornar vazio, caso não exista um cliente para o id e usuario informados")
        void shouldReturnEmptyWhenClientNotFoundByIdAndBroker() {
            createClient(user , "Client 1" , "client1@email.com");
            Optional<Client> client = clientRepository.findByIdAndBroker(2L, user);
            assertThat(client).isNotPresent();
        }
    }

}
