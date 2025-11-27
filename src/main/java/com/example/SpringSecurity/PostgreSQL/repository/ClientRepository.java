package com.example.SpringSecurity.PostgreSQL.repository;

import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SpringSecurity.PostgreSQL.domain.entity.Client;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByBroker(User broker);
    Optional<Client> findByIdAndBroker(Long id, User broker);
}
