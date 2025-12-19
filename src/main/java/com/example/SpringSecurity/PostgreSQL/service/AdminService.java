package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.domain.dto.response.UserResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions.UserDeleteException;
import com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions.UserUpdateException;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> new UserResponse(
                        user.getName(),
                        user.getEmail()
                ));
    }

    @Transactional
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Usuario nao encontrado");
        }
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new UserDeleteException("Erro ao deletar usuario - " + e.getMessage());
        }
    }

    @Transactional
    public void disableUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Usuario nao encontrado");
        }
        try {
            User userToDisable = user.get();
            userToDisable.setEnabled(false);
            userRepository.save(userToDisable);
        } catch (Exception e) {
            throw new UserUpdateException("Erro ao desabilitar usuario - " + e.getMessage());
        }
    }
}
