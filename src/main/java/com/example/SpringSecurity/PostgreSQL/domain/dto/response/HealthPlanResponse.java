package com.example.SpringSecurity.PostgreSQL.domain.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record HealthPlanResponse(String name,
                                 String operator,
                                 String operatorCode,
                                 BigDecimal basePrice,
                                 Map<String, Double> ageFactor,
                                 String coverage
                                ) {

}
