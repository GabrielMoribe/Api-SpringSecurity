package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.UpdateHealthPlanRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.HealthPlanResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.HealthPlan;
import com.example.SpringSecurity.PostgreSQL.repository.HealthPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthPlanService {
    @Autowired
    private HealthPlanRepository healthPlanRepository;

    private HealthPlanResponse mapToResponse(HealthPlan plan) { //Conversao de HealthPlan para HealthPlanResponse
        return new HealthPlanResponse(
                plan.getName(),
                plan.getOperator(),
                plan.getOperatorCode(),
                plan.getBasePrice(),
                plan.getAgeFactor(),
                plan.getCoverage()
        );
    }


    public List<HealthPlanResponse> findAll() {
        return healthPlanRepository.findAll()
                .stream()
                .map(plans-> mapToResponse(plans))
                .toList();
    }

    // Services para funcionalidades de admin(Implementacao futura)


}
