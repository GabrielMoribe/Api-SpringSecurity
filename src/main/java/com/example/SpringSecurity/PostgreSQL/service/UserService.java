package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.config.JWTUserData;
import com.example.SpringSecurity.PostgreSQL.domain.dto.request.UpdateUserRequest;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.exceptions.authExceptions.EmailAlreadyRegisteredException;
import com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions.UserDeleteException;
import com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions.UserNotAuthenticatedException;
import com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions.UserRetrievalException;
import com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions.UserUpdateException;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JWTUserData userData) {
            return userRepository.findById(userData.userId())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
        }
        throw new UserNotAuthenticatedException("Usuario nao autenticado");
    }


    public User updateUser(UpdateUserRequest request) {
        try{
            User user = getAuthenticatedUser();

            if (request.name() != null && !request.name().isEmpty()) {
                user.setName(request.name());
            }
            if (request.email() != null && !request.email().isEmpty()) {
                if (!request.email().equals(user.getEmail()) && userRepository.findUserByEmail(request.email()).isPresent()) {
                    throw new EmailAlreadyRegisteredException("Este email já está em uso.");
                }
                user.setEmail(request.email());
            }
            return userRepository.save(user);
        }catch(Exception e){
            throw new UserUpdateException("Erro ao atualizar usuario - " + e.getMessage());
        }
    }

    public void deleteUser() {
        try{
            User user = getAuthenticatedUser();
            user.setEnabled(false);
            userRepository.delete(user);
        }catch(Exception e){
            throw new UserDeleteException("Erro ao deletar usuario - " + e.getMessage());
        }
    }

    public User findUser() {
        try{
            return getAuthenticatedUser();
        }catch (Exception e){
            throw new UserRetrievalException("Erro ao recuperar usuario - " + e.getMessage());
        }
    }
}
