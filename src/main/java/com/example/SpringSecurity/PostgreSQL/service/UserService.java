package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.config.JWTUserData;
import com.example.SpringSecurity.PostgreSQL.domain.dto.request.UpdateUserRequest;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.exceptions.authExceptions.EmailAlreadyRegisteredException;
import com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions.InvalidEmailChangeTokenException;
import com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions.UserDeleteException;
import com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions.UserNotAuthenticatedException;
import com.example.SpringSecurity.PostgreSQL.exceptions.userExceptions.UserUpdateException;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JWTUserData userData) {
            return userRepository.findById(userData.userId())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
        }
        throw new UserNotAuthenticatedException("Usuario nao autenticado");
    }

    @Transactional
    public User updateUser(UpdateUserRequest request) {
        User user = getAuthenticatedUser();
        if (request.name() != null && !request.name().isEmpty()) {
            user.setName(request.name());
        }
        if (request.email() != null && !request.email().isEmpty()) {
            if (!request.email().equals(user.getEmail()) && userRepository.findUserByEmail(request.email()).isPresent()) {
                throw new EmailAlreadyRegisteredException("Este email já está em uso.");
            }
            //user.setEmail(request.email());
        }
        try{
            return userRepository.save(user);
        }catch(Exception e){
            throw new UserUpdateException("Erro ao atualizar usuario - " + e.getMessage());
        }
    }

    @Transactional
    public void changeEmail(String newEmail){
        User user = getAuthenticatedUser();
        if(user.getEmail().equalsIgnoreCase(newEmail)){
            throw new EmailAlreadyRegisteredException("O novo email deve ser diferente do email atual.");
        }
        if(userRepository.findUserByEmail(newEmail).isPresent()){
            throw new EmailAlreadyRegisteredException("Este email ja esta em uso.");
        }
        String token = UUID.randomUUID().toString();
        user.setNewEmailPlaceholder(newEmail.toLowerCase());
        user.setNewEmailToken(token);
        user.setNewEmailTokenExpiresAt(LocalDateTime.now().plusMinutes(3));
        try{
            userRepository.save(user);
        } catch (Exception e) {
            throw new UserUpdateException("Erro ao atualizar usuario - " + e.getMessage());
        }
        sendConfirmEmailChange(newEmail , user.getName() , token);
    }

    @Transactional
    public void confirmEmailChange(String token){
        User user = userRepository.findByNewEmailToken(token)
                .orElseThrow(() -> new InvalidEmailChangeTokenException("Token inválido ou não encontrado."));
        if (user.getNewEmailTokenExpiresAt() != null && user.getNewEmailTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidEmailChangeTokenException("Token expirado. Solicite a troca novamente.");
        }
        String newEmail = user.getNewEmailPlaceholder();
        user.setEmail(newEmail);
        user.setNewEmailPlaceholder(null);
        user.setNewEmailToken(null);
        user.setNewEmailTokenExpiresAt(null);
        try{
            userRepository.save(user);
        }catch(Exception e){
            throw new UserUpdateException("Erro ao atualizar usuario - " + e.getMessage());
        }
    }



    public void sendConfirmEmailChange(String newEmail , String username , String token) {
        String subject = "Verificando email";
        String confirmationUrl = "http://localhost:8080/users/profile/change-email/confirm?token=" + token;
        String htmlMessage = "<!DOCTYPE html>"
                + "<html><body>"
                + "<h2>Confirme seu novo e-mail</h2>"
                + "<p>Olá " + (username != null ? username : "") + ",</p>"
                + "<p>Recebemos um pedido para alterar seu e-mail para: <b>" + newEmail + "</b></p>"
                + "<p>Para confirmar, clique no link abaixo:</p>"
                + "<a href=\"" + confirmationUrl + "\">Confirmar Alteração</a>"
                + "<p>Este link expira em 3 minutos.</p>"
                + "</body></html>";
        emailService.sendVerificationEmail(newEmail, subject, htmlMessage);
    }

    @Transactional
    public void deleteUser() {
        User user = getAuthenticatedUser();
        //user.setEnabled(false);
        try{
            userRepository.delete(user);
        }catch(Exception e){
            throw new UserDeleteException("Erro ao deletar usuario - " + e.getMessage());
        }
    }

    public User findUser() {
        return getAuthenticatedUser();
    }

}
