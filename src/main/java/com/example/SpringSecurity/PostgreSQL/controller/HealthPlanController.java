package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.CreateHealthPlanRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ApiResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.HealthPlanResponse;
import com.example.SpringSecurity.PostgreSQL.service.HealthPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/health-plans")
public class HealthPlanController {

    private final HealthPlanService healthPlanService;

    public HealthPlanController(HealthPlanService healthPlanService) {
        this.healthPlanService = healthPlanService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<HealthPlanResponse>>> getAllHealthPlans(){
        List<HealthPlanResponse> plans = healthPlanService.findAll();
        ApiResponse<List<HealthPlanResponse>> response = ApiResponse.success(plans);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



    //ADMIN
    @PostMapping("/newHealthPlan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<HealthPlanResponse>> createHealthPlan(@Valid @RequestBody CreateHealthPlanRequest createHealthPlanRequest){
        HealthPlanResponse healthPlanResponse = healthPlanService.createHealthPlan(createHealthPlanRequest);
        ApiResponse<HealthPlanResponse> response = ApiResponse.success(healthPlanResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/updateHealthPlan/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<HealthPlanResponse>> updateHealthPlan(@PathVariable Long id, @Valid @RequestBody CreateHealthPlanRequest createHealthPlanRequest){
        HealthPlanResponse healthPlanResponse = healthPlanService.updateHealthPlan(id, createHealthPlanRequest);
        ApiResponse<HealthPlanResponse> response = ApiResponse.success(healthPlanResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/deleteHealthPlan/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteHealthPlan(@PathVariable Long id) {
        healthPlanService.deleteHealthPlan(id);
        ApiResponse<String> response = ApiResponse.success("Plano de saude deletado com sucesso");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
