package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.config.TokenConfig;
import com.example.SpringSecurity.PostgreSQL.domain.dto.request.LoginRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.request.RegUserRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.request.VerifyUserRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.LoginResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.RegUserResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.VerifyUserResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.exceptions.authExceptions.*;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,@Lazy AuthenticationManager authenticationManager,TokenConfig tokenConfig,EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
        this.emailService = emailService;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
    }

    private RegUserResponse mapToResponse(User user) {
        return new RegUserResponse(user.getName(), user.getEmail());
    }




    public LoginResponse login(LoginRequest request) {

            String lowerCaseEmail = request.email().toLowerCase();
//            if(!userRepository.findUserByEmail(lowerCaseEmail).isPresent()){
//                throw new UsernameNotFoundException("Usuario nao cadastrado");
//           }
//            else{
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(lowerCaseEmail, request.password());
                Authentication authentication = authenticationManager.authenticate(authToken);
                User user = (User) authentication.getPrincipal();
                String token = tokenConfig.generateToken(user);
                return new LoginResponse(token);
//            }


    }


    public RegUserResponse register(RegUserRequest request) {
        Optional<User> existingUserOpt = userRepository.findUserByEmail(request.email());
        if(existingUserOpt.isPresent()){
            if(existingUserOpt.get().isEnabled()){
                throw new EmailAlreadyRegisteredException("Email ja cadastrado");
            }
            else{
                User existingUser = existingUserOpt.get();
                existingUser.setName(request.name());
                existingUser.setEmail(request.email().toLowerCase());
                existingUser.setPassword(passwordEncoder.encode(request.password()));
                existingUser.setEnabled(false);
                existingUser.setVerificationCode(generateVerificationCode());
                existingUser.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(3));
                sendVerificationEmail(existingUser);
                userRepository.save(existingUser);
                return mapToResponse(existingUser);
            }
        }
        User newUser = new User();
        newUser.setName(request.name());
        newUser.setEmail(request.email().toLowerCase());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setEnabled(false);
        newUser.setVerificationCode(generateVerificationCode());
        newUser.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(3));
        sendVerificationEmail(newUser);
        userRepository.save(newUser);
        return mapToResponse(newUser);
    }


    public VerifyUserResponse verifyUser(VerifyUserRequest request) {
        Optional<User> userOpt = userRepository.findUserByEmail(request.email());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if(user.getVerificationExpiresAt().isBefore(LocalDateTime.now())) {
                throw new ExpiredVerificationCodeException("Codigo expirado");
            }
            if(user.getVerificationCode().equals(request.verificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationExpiresAt(null);
                userRepository.save(user);
            }
            else{
                throw new InvalidVerificationCode("Codigo Invalido!");
            }
        }
        else {
            throw new UsernameNotFoundException("Usuario nao encontrado");
        }
        return new VerifyUserResponse("Usuario verificado com sucesso");
    }


    public void resendVerificationCode(String email){
        Optional<User> userOpt = userRepository.findUserByEmail(email);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            if(user.isEnabled()){
                throw new UserAlreadyVerified("Usuario ja verificado");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(3));
            sendVerificationEmail(user);
            userRepository.save(user);
        }
        else{
            throw new UsernameNotFoundException("Usuario nao encontrado");
        }
    }


    public void sendVerificationEmail(User user){
        String subject = "Ativacao de Conta.";
        String verifyUrl = "http://localhost:8080/api/auth/verify?email=" + user.getEmail() + "&code=" + user.getVerificationCode();
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<!DOCTYPE html>"
                + "<html lang=\"pt-BR\">"
                + "<head>"
                + "<meta charset=\"UTF-8\"/>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>"
                + "<title>Ativação de Conta</title>"
                + "</head>"
                + "<body style=\"font-family: Arial, sans-serif; color:#333;\">"
                + "<div style=\"max-width:600px;margin:0 auto;padding:20px;border:1px solid #eaeaea;border-radius:8px;\">"
                + "<h2 style=\"color:#2d6cdf;margin-top:0;\">Ative sua conta</h2>"
                + "<p>Olá " + (user.getName() != null ? user.getName() : "") + ",</p>"
                + "<p>Use o código abaixo para verificar seu e-mail. Ele expira em 3 minutos.</p>"
                + "<p style=\"font-size:20px;font-weight:bold;background:#f5f5f5;padding:10px;border-radius:4px;display:inline-block;\">" + verificationCode + "</p>"
                + "<p style=\"margin-top:20px;\">Ou clique no botão abaixo para validar automaticamente:</p>"
                + "<p><a href=\"" + verifyUrl + "\" style=\"display:inline-block;padding:10px 16px;background:#2d6cdf;color:#fff;text-decoration:none;border-radius:4px;\">Verificar Conta</a></p>"
                + "<hr/>"
                + "<p style=\"font-size:12px;color:#777;\">Se você não solicitou este e-mail, ignore-o.</p>"
                + "</div>"
                + "</body>"
                + "</html>";
        emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
    }

    private String generateVerificationCode(){
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}