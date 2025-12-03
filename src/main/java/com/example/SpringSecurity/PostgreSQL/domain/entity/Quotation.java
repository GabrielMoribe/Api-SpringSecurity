package com.example.SpringSecurity.PostgreSQL.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "quotations")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Quotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "health_plan_id", nullable = false)
    private HealthPlan healthPlan;

    @Column(name = "final_price", nullable = false)
    private BigDecimal finalPrice;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "beneficiaries_by_age", columnDefinition = "jsonb")
    private Map<String, Integer> beneficiariesByAge;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
