package com.example.SpringSecurity.PostgreSQL.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record QuotationResponse(String clientName,
                                String healthPlanName,
                                String operator,
                                BigDecimal finalPrice,
                                LocalDateTime createdAt) {
}
