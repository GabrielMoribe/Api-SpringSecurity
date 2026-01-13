package com.example.SpringSecurity.PostgreSQL.config;

import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.domain.enums.Roles;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!test")
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdmin();
        seedUser();
    }

    private void seedAdmin() {
        if (userRepository.findUserByEmail("admin@email.com").isEmpty()) {
            User admin = new User();
            admin.setName("admin");
            admin.setEmail("admin@email.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Roles.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
        }
    }

    private void seedUser() {
        if (userRepository.findUserByEmail("user@email.com").isEmpty()) {
            User user = new User();
            user.setName("user");
            user.setEmail("user@email.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(Roles.USER);
            user.setEnabled(true);
            userRepository.save(user);
        }
    }
}
