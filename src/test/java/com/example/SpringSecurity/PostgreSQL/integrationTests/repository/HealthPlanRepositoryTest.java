package com.example.SpringSecurity.PostgreSQL.integrationTests.repository;

import com.example.SpringSecurity.PostgreSQL.domain.entity.HealthPlan;
import com.example.SpringSecurity.PostgreSQL.repository.HealthPlanRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class HealthPlanRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private HealthPlanRepository healthPlanRepository;

    @BeforeEach
    void setUp() {
        healthPlanRepository.deleteAll();

        HealthPlan healthPlan1 = new HealthPlan();
        healthPlan1.setName("Basic Plan");
        healthPlan1.setOperator("Unimed");
        healthPlan1.setOperatorCode("UNIMED-001");
        healthPlan1.setBasePrice(new BigDecimal("299.90"));
        healthPlan1.setAgeFactor(Map.of(
                "0-18", 0.8,
                "19-29", 1.0,
                "30-49", 1.3,
                "50+", 1.8
        ));
        healthPlan1.setCoverage("Full Coverage");

        HealthPlan healthPlan2 = new HealthPlan();
        healthPlan2.setName("Premium Plan");
        healthPlan2.setOperator("Bradesco");
        healthPlan2.setOperatorCode("BRADESCO-001");
        healthPlan2.setBasePrice(new BigDecimal("299.90"));
        healthPlan2.setAgeFactor(Map.of(
                "0-18", 0.8,
                "19-29", 1.0,
                "30-49", 1.3,
                "50+", 1.8
        ));
        healthPlan2.setCoverage("Full Coverage");

        healthPlanRepository.save(healthPlan1);
        healthPlanRepository.save(healthPlan2);
    }

    @Nested
    @DisplayName("Metodo findByNameAndOperator")
    class FindByNameAndOperator {
        @Test
        @DisplayName("Deve retornar o plano de saude por nome e operadora")
        void shouldReturnHealthPlanByNameAndOperator() {
            Optional<HealthPlan> healthPlan = healthPlanRepository.findByNameAndOperator("Basic Plan", "Unimed");
            assertThat(healthPlan).isPresent();
            assertThat(healthPlan.get().getOperatorCode()).isEqualTo("UNIMED-001");
        }
        @Test
        @DisplayName("Deve retornar vazio quando nome correto mas operadora incorreta")
        void shouldReturnEmptyWhenNameCorrectButOperatorIncorrect() {
            Optional<HealthPlan> healthPlan = healthPlanRepository.findByNameAndOperator("Basic Plan", "Teste");
            assertThat(healthPlan).isEmpty();
        }
        @Test
        @DisplayName("Deve retornar vazio quando operadora correta mas nome incorreto")
        void shouldReturnEmptyWhenOperatorCorrectButNameIncorrect() {
            Optional<HealthPlan> healthPlan = healthPlanRepository.findByNameAndOperator("Teste", "Unimed");
            assertThat(healthPlan).isEmpty();
        }
    }
}
