package com.example.SpringSecurity.PostgreSQL.repository;

import com.example.SpringSecurity.PostgreSQL.domain.entity.HealthPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthPlanRepository extends JpaRepository<HealthPlan, Long> {

}
