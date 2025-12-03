package com.example.SpringSecurity.PostgreSQL.repository;

import com.example.SpringSecurity.PostgreSQL.domain.entity.Quotation;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation,Long> {
    List<Quotation> findByClient_Broker(User broker);
    Optional<Quotation> findByIdAndClient_Broker(Long id, User broker);
}
