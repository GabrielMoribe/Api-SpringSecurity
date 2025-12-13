package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.CreateHealthPlanRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.HealthPlanResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.HealthPlan;
import com.example.SpringSecurity.PostgreSQL.exceptions.healthPlanExceptions.HealthPlanRetrievalException;
import com.example.SpringSecurity.PostgreSQL.repository.HealthPlanRepository;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // ADMIN
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public HealthPlanResponse createHealthPlan(CreateHealthPlanRequest request) {
        if(healthPlanRepository.findByNameAndOperator(request.name(), request.operator()).isPresent()){
            throw new IllegalArgumentException("Plano com este nome ja existe para esta operadora");
        }
        HealthPlan newPlan = new HealthPlan();
        newPlan.setName(request.name());
        newPlan.setOperator(request.operator());
        newPlan.setOperatorCode(request.operatorCode());
        newPlan.setAgeFactor(request.ageFactor());
        newPlan.setBasePrice(request.basePrice());
        newPlan.setCoverage(request.coverage());
        try{
            healthPlanRepository.save(newPlan);
        }catch(Exception e ){
            throw new RuntimeException("Erro ao criar plano de saude - " + e.getMessage()); //CRiar excecao
        }
        return mapToResponse(newPlan);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public HealthPlanResponse updateHealthPlan(Long id , CreateHealthPlanRequest request) {

        HealthPlan existingPlan = healthPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plano de saude nao encontrado"));

        if(healthPlanRepository.findByNameAndOperator(request.name(), request.operator()).isPresent()){
            throw new IllegalArgumentException("Plano com este nome ja existe para esta operadora");
        }

        existingPlan.setName(request.name());
        existingPlan.setOperator(request.operator());
        existingPlan.setOperatorCode(request.operatorCode());
        existingPlan.setAgeFactor(request.ageFactor());
        existingPlan.setBasePrice(request.basePrice());
        existingPlan.setCoverage(request.coverage());
        try{
            healthPlanRepository.save(existingPlan);
        }catch(Exception e ){
            throw new RuntimeException("Erro ao atualizar plano de saude - " + e.getMessage()); //criar excecao
        }
        return mapToResponse(existingPlan);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteHealthPlan(Long id) {

        if(healthPlanRepository.findById(id).isEmpty()){
            throw new IllegalArgumentException("Plano de saude nao encontrado");
        }
        try{
            healthPlanRepository.deleteById(id);
        }catch(Exception e ){
            throw new RuntimeException("Erro ao deletar plano de saude - " + e.getMessage()); //criar excecao
        }
    }

}
