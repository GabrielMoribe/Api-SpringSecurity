package com.example.SpringSecurity.PostgreSQL.repository;

import com.example.SpringSecurity.PostgreSQL.domain.entity.HealthPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthPlanRepository extends JpaRepository<HealthPlan, Long> {
    Optional<HealthPlan> findByNameAndOperator(String name, String operator);
}
