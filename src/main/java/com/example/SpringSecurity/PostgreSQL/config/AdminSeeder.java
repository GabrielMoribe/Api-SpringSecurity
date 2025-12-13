package com.example.SpringSecurity.PostgreSQL.config;

import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.domain.enums.Roles;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Optional<User> userAdmin = userRepository.findUserByEmail("admin@email.com");

        if (userAdmin.isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@email.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Roles.ADMIN);
            admin.setEnabled(true);
            try{
                userRepository.save(admin);
            } catch(Exception e){
                throw new RuntimeException("Erro ao criar usuario admin - " + e.getMessage());
            }
        }
    }
}