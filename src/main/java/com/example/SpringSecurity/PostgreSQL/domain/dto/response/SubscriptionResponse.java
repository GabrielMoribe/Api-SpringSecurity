package com.example.SpringSecurity.PostgreSQL.domain.dto.response;

import com.example.SpringSecurity.PostgreSQL.domain.enums.SubscriptionStatus;

public record SubscriptionResponse(
        SubscriptionStatus status,
        String checkoutUrl,
        String message
) {}
