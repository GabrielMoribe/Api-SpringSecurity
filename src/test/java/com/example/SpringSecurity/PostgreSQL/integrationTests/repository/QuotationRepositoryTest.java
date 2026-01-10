package com.example.SpringSecurity.PostgreSQL.integrationTests.repository;

import com.example.SpringSecurity.PostgreSQL.domain.entity.Client;
import com.example.SpringSecurity.PostgreSQL.domain.entity.HealthPlan;
import com.example.SpringSecurity.PostgreSQL.domain.entity.Quotation;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.domain.enums.Roles;
import com.example.SpringSecurity.PostgreSQL.repository.ClientRepository;
import com.example.SpringSecurity.PostgreSQL.repository.HealthPlanRepository;
import com.example.SpringSecurity.PostgreSQL.repository.QuotationRepository;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class QuotationRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private QuotationRepository quotationRepository;
    @Autowired
    private HealthPlanRepository healthPlanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;


    @BeforeEach
    void setUp() {
        quotationRepository.deleteAll();
        healthPlanRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();


        HealthPlan healthPlan1 = new HealthPlan();
        healthPlan1.setName("Basic Plan");
        healthPlan1.setOperator("Unimed");
        healthPlan1.setOperatorCode("UNIMED-001");
        healthPlan1.setBasePrice(new BigDecimal("299.90"));
        healthPlan1.setCoverage("Full Coverage");
        healthPlan1.setAgeFactor(Map.of(
                "0-18", 0.8,
                "19-29", 1.0,
                "30-49", 1.3,
                "50+", 1.8
        ));
        healthPlanRepository.save(healthPlan1);


        user = new User();
        user.setName("user");
        user.setEmail("user@email.com");
        user.setPassword("123123");
        user.setEnabled(true);
        user.setRole(Roles.USER);
        userRepository.save(user);


        Client client = new Client();
        client.setName("Client");
        client.setEmail("client@email.com");
        client.setPhone("9999-9999");
        client.setBroker(user);
        clientRepository.save(client);


        Quotation quotation = new Quotation();
        quotation.setClient(client);
        quotation.setHealthPlan(healthPlan1);
        quotation.setFinalPrice(new BigDecimal("350.00"));
        quotation.setBeneficiariesByAge(Map.of(
                "0-18", 2,
                "19-29", 1
        ));
        quotationRepository.save(quotation);
    }



    @Nested
    @DisplayName("Metodo findByClient_Broker")
    class findByClient_Broker {
        @Test
        @DisplayName("Deve retornar cotações por corretor")
        void shouldReturnQuotationsByBroker(){
            List<Quotation> quotations = quotationRepository.findByClient_Broker(user);
            assertThat(quotations).isNotEmpty();
            assertThat(quotations.size()).isEqualTo(1);
        }
        @Test
        @DisplayName("Deve retornar vazio quando o corretor não possuir cotações")
        void shouldReturnEmptyWhenBrokerHasNoQuotations() {
            User newUser = new User();
            userRepository.save(newUser);
            List<Quotation> quotations = quotationRepository.findByClient_Broker(newUser);
            assertThat(quotations).isEmpty();
        }
    }


    @Nested
    @DisplayName("Metodo findByIdAndClient_Broker")
    class findByIdAndClient_Broker {
        @Test
        @DisplayName("Deve retornar cotação por id e corretor")
        void shouldReturnQuotationByIdAndBroker() {
            Optional<Quotation> quotation = quotationRepository.findByIdAndClient_Broker(user.getId(), user);
            assertThat(quotation).isPresent();
        }

        @Test
        @DisplayName("Deve retornar vazio quando não existir cotação para o id e corretor")
        void shouldReturnEmptyWhenNoQuotationForIdAndBroker() {
            Optional<Quotation> quotation = quotationRepository.findByIdAndClient_Broker(2L, user);
            assertThat(quotation).isNotPresent();
        }
    }

}
