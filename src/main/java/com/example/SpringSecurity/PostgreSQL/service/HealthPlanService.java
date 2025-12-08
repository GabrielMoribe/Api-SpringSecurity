package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.domain.dto.response.HealthPlanResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.HealthPlan;
import com.example.SpringSecurity.PostgreSQL.exceptions.healthPlanExceptions.HealthPlanRetrievalException;
import com.example.SpringSecurity.PostgreSQL.repository.HealthPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HealthPlanService {

    private final HealthPlanRepository healthPlanRepository;

    public HealthPlanService(HealthPlanRepository healthPlanRepository) {
        this.healthPlanRepository = healthPlanRepository;
    }

    private HealthPlanResponse mapToResponse(HealthPlan plan) {
        return new HealthPlanResponse(
                plan.getName(),
                plan.getOperator(),
                plan.getOperatorCode(),
                plan.getBasePrice(),
                plan.getAgeFactor(),
                plan.getCoverage()
        );
    }


    @Transactional(readOnly = true)
    public List<HealthPlanResponse> findAll() {
        try{
            return healthPlanRepository.findAll()
                    .stream()
                    .map(plans-> mapToResponse(plans))
                    .toList();
        } catch (Exception e) {
            throw new HealthPlanRetrievalException("Erro ao recuperar planos de saude - " + e.getMessage());
        }

    }

    // Services para funcionalidades de admin(Implementacao futura)


}
