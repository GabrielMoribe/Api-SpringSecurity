package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ApiResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.HealthPlanResponse;
import com.example.SpringSecurity.PostgreSQL.service.HealthPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/health-plans")
public class HealthPlanController {

    private final HealthPlanService healthPlanService;

    public HealthPlanController(HealthPlanService healthPlanService) {
        this.healthPlanService = healthPlanService;
    }

    @GetMapping("/all")
    private ResponseEntity<ApiResponse<List<HealthPlanResponse>>> getAllHealthPlans(){
        List<HealthPlanResponse> plans = healthPlanService.findAll();
        ApiResponse<List<HealthPlanResponse>> response = ApiResponse.success(plans);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



    //Endpoints para admins (Implementacao futura)



}
