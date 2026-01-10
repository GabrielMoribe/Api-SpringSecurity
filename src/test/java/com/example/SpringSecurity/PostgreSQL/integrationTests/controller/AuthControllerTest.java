package com.example.SpringSecurity.PostgreSQL.integrationTests.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.*;
import com.example.SpringSecurity.PostgreSQL.domain.entity.RefreshToken;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.domain.enums.Roles;
import com.example.SpringSecurity.PostgreSQL.repository.RefreshTokenRepository;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private User user1;
    private User user2;


    @BeforeEach
    void setUp(){
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@email.com");
        user1.setPassword(passwordEncoder.encode("123123"));
        user1.setEnabled(true);
        user1.setRole(Roles.USER);
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@email.com");
        user2.setPassword(passwordEncoder.encode("123123"));
        user2.setEnabled(false);
        user2.setRole(Roles.USER);
        user2 = userRepository.save(user2);
    }


    @Nested
    @DisplayName("endpoint: /register")
    class Register {
        @Test
        @DisplayName("Deve registrar um usuario com sucesso")
        void shoudRegisterUserSuccessfully() throws Exception {
            RegUserRequest regUserRequest = new RegUserRequest("newUser", "newUser@email.com", "123123");
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(regUserRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("newUser"))
                    .andExpect(jsonPath("$.data.email").value("newuser@email.com"));
        }
        @Test
        @DisplayName("Deve falhar ao registrar um usuario com email ja cadastrado")
        void shouldFailWhenRegisterUserWithExistingEmail() throws Exception {
            RegUserRequest regUserRequest = new RegUserRequest("user", "user1@email.com", "123123");
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(regUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Email ja cadastrado"));
        }
        @Test
        @DisplayName("Deve falhar ao registrar um usuario com email invalido")
        void shouldFailWhenRegisteringAnInvalidEmail() throws Exception {
            RegUserRequest regUserRequest = new RegUserRequest("user", "useremail.com", "123123");
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(regUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro de validação: email deve ser valido"));
        }
        @Test
        @DisplayName("Deve falhar ao registrar um usuario com nome invalido")
        void shouldFailWhenRegisteringAnInvalidName() throws Exception {
            RegUserRequest regUserRequest = new RegUserRequest("u", "user@email.com", "123123");
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(regUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro de validação: Nome deve ter entre 3 e 20 caracteres"));
        }
        @Test
        @DisplayName("Deve falhar ao registrar um usuario com senha invalida")
        void shouldFailWhenRegisteringAnInvalidPassword() throws Exception {
            RegUserRequest regUserRequest = new RegUserRequest("user", "user@email.com", null);
            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(regUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro de validação: Senha é obrigatoria"));
        }
    }



    @Nested
    @DisplayName("endpoint: /verify")
    class verify {
        @Test
        @DisplayName("Deve verificar o usuario com sucesso")
        void shouldVerifyUserSuccessfully() throws Exception {
            user2.setVerificationCode("123123");
            user2.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user2);
            VerifyUserRequest verifyUserRequest = new VerifyUserRequest("user2@email.com", "123123");
            mockMvc.perform(post("/auth/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(verifyUserRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Operação realizada com sucesso"));
        }
        @Test
        @DisplayName("Deve falhar ao verificar uma conta ja verificada")
        void shouldFailWhenVerifyingAnAlreadyVerifiedAccount() throws Exception {
            user1.setVerificationCode("123123");
            user1.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user1);
            VerifyUserRequest verifyUserRequest = new VerifyUserRequest("user1@email.com", "123123");
            mockMvc.perform(post("/auth/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(verifyUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Usuario ja verificado"));
        }
        @Test
        @DisplayName("Deve falhar ao verificar com um codigo ja expirado")
        void shouldFailWhenTryingToVerifyWithAnExpiredCode() throws Exception {
            user2.setVerificationCode("123123");
            user2.setVerificationExpiresAt(LocalDateTime.now().minusMinutes(1));
            userRepository.save(user2);
            VerifyUserRequest verifyUserRequest = new VerifyUserRequest("user2@email.com", "123123");
            mockMvc.perform(post("/auth/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(verifyUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Codigo expirado"));
        }
        @Test
        @DisplayName("Deve falhar ao verificar com codigo incorreto")
        void shouldFailWhenVerifyingWithWrongCode() throws Exception {
            user2.setVerificationCode("123123");
            user2.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user2);
            VerifyUserRequest verifyUserRequest = new VerifyUserRequest("user2@email.com", "999999");
            mockMvc.perform(post("/auth/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(verifyUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Codigo Invalido!"));
        }
        @Test
        @DisplayName("Deve falhar ao verificar um email nao cadastrado")
        void shouldFailWhenVerifyingAnUnregisteredEmail() throws Exception {
            VerifyUserRequest verifyUserRequest = new VerifyUserRequest("unregisteredUser@email.com", "123123");
            mockMvc.perform(post("/auth/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(verifyUserRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Usuario nao encontrado"));
        }
        @Test
        @DisplayName("Deve falhar ao verificar um email invalido")
        void shouldFailWhenVerifyingAnInvalidEmail() throws Exception {
            VerifyUserRequest verifyUserRequest = new VerifyUserRequest("invalidEmail.com", "123123");
            mockMvc.perform(post("/auth/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(verifyUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro de validação: Por favor insira um email valido"));
        }
        @Test
        @DisplayName("Deve falhar ao verificar um codigo invalido")
        void shouldFailWhenVerifyingWithAnInvalidCode() throws Exception {
            VerifyUserRequest verifyUserRequest = new VerifyUserRequest("user1@Email.com",  null);
            mockMvc.perform(post("/auth/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(verifyUserRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro de validação: Por favor insira uma codigo de verificacao"));
        }
    }



    @Nested
    @DisplayName("endpoint: /verify-account")
    class verifyAccount {
        @Test
        @DisplayName("Deve verificar o usuario via query params com sucesso")
        void shouldVerifyUserViaQueryParamsSuccessfully() throws Exception {
            user2.setVerificationCode("123123");
            user2.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user2);
            mockMvc.perform(get("/auth/verify-account")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("email", "user2@email.com")
                            .param("code", "123123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Operação realizada com sucesso"));
        }
        @Test
        @DisplayName("Deve falhar verificacao caso query params estejam ausentes")
        void shouldFailIfQueryParamsAreMissing() throws Exception {
            mockMvc.perform(get("/auth/verify-account")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Parâmetro obrigatório ausente: email"));
        }
    }



    @Nested
    @DisplayName("endpoint: /verify-account/resend")
    class resend {
        @Test
        @DisplayName("Deve reenviar o codigo de verificacao com sucesso")
        void shouldResendVerificationCodeSuccessfully() throws Exception {
            EmailRequest emailRequest = new EmailRequest("user2@email.com");
            mockMvc.perform(post("/auth/verify-account/resend")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emailRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Operação realizada com sucesso"));
        }
        @Test
        @DisplayName("Deve falhar ao reenviar codigo para um email ja verificado")
        void shouldFailWhenResendingCodeToAnAlreadyVerifiedEmail() throws Exception {
            EmailRequest emailRequest = new EmailRequest("user1@email.com");
            mockMvc.perform(post("/auth/verify-account/resend")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emailRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Usuario ja verificado"));
        }
        @Test
        @DisplayName("Deve falhar ao reenviar codigo para um email nao registrado")
        void shouldFailWhenResendingCodeToAnUnregisteredEmail() throws Exception {
            EmailRequest emailRequest = new EmailRequest("unregisteredMail@email.com");
            mockMvc.perform(post("/auth/verify-account/resend")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emailRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Usuario nao encontrado"));
        }

        @Test
        @DisplayName("Deve falhar ao reenviar codigo para um email invalido")
        void shouldFailWhenResendingCodeToAnInvalidEmail() throws Exception {
            EmailRequest emailRequest = new EmailRequest("invalidEmail.com");
            mockMvc.perform(post("/auth/verify-account/resend")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emailRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro de validação: Email deve ser válido"));
        }
    }





    @Nested
    @DisplayName("endpoint: /login")
    class login {
        @Test
        @DisplayName("Deve logar o usuario com sucesso")
        void shouldLoginUserSuccessfully() throws Exception {
            LoginRequest loginRequest = new LoginRequest("user1@email.com" , "123123");
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Operação realizada com sucesso"));
        }
        @Test
        @DisplayName("Deve falhar ao logar com senha incorreta")
        void shouldFailWhenLoggingInWithIncorrectPassword() throws Exception {
            LoginRequest loginRequest = new LoginRequest("user1@email.com" , "wrongPassword");
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Email ou senha incorretos."));
        }
        @Test
        @DisplayName("Deve falhar ao logar com email nao registrado")
        void shouldFailWhenLoggingInWithUnregisteredEmail() throws Exception {
            LoginRequest loginRequest = new LoginRequest("UnregisteredUser@email.com" , "123123");
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Email ou senha incorretos."));
        }
        @Test
        @DisplayName("Deve falhar ao logar com usuario nao verificado")
        void shouldFailWhenLoggingInWithUnverifiedUser() throws Exception {
            LoginRequest loginRequest = new LoginRequest("user2@email.com" , "123123");
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Sua conta ainda não foi verificada. Por favor, verifique seu e-mail ou cadastre-se novamente."));
        }
        @Test
        @DisplayName("Deve falhar ao logar com email invalido")
        void shouldFailWhenLoggingInWithInvalidEmail() throws Exception {
            LoginRequest loginRequest = new LoginRequest("invalidEmail.com" , "123123");
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro de validação: email deve ser valido"));
        }
        @Test
        @DisplayName("Deve falhar ao logar com a senha invalida")
        void shouldFailWhenLoggingInWithInvalidPassword() throws Exception {
            LoginRequest loginRequest = new LoginRequest("user1@email.com" , "123");
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro de validação: Senha deve ter no minimo 6 caracteres"));
        }
    }



    @Nested
    @DisplayName("endpoint: /forgot-password")
    class forgotPassword {
        @Test
        @DisplayName("Deve enviar email de redefinicao de senha com sucesso")
        void shouldSendPasswordResetEmailSuccessfully() throws Exception {
            EmailRequest emailRequest = new EmailRequest("user1@email.com");
            mockMvc.perform(post("/auth/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emailRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Operação realizada com sucesso"))
                    .andExpect(jsonPath("$.data").value("Um email foi enviado para " + emailRequest.email() + " com instruções para redefinir sua senha."));
        }
        @Test
        @DisplayName("Deve falhar ao solicitar redefinicao de senha para email nao registrado")
        void shouldFailWhenRequestingPasswordResetForUnregisteredEmail() throws Exception {
            EmailRequest emailRequest = new EmailRequest("unregistered@email.com");
            mockMvc.perform(post("/auth/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emailRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Usuario nao encontrado"));
        }
        @Test
        @DisplayName("Deve falhar ao solicitar redefinicao de senha para conta nao verificada")
        void shouldFailWhenRequestingPasswordResetForUnverifiedAccount() throws Exception {
            EmailRequest emailRequest = new EmailRequest("user2@email.com");
            mockMvc.perform(post("/auth/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emailRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Usuario nao verificado"));
        }
        @Test
        @DisplayName("Deve falhar ao solicitar redefinicao de senha para email invalido")
        void shouldFailWhenRequestingPasswordResetForInvalidEmail() throws Exception {
            EmailRequest emailRequest = new EmailRequest("invalidEmail.com");
            mockMvc.perform(post("/auth/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emailRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro de validação: Email deve ser válido"));
        }

    }



    @Nested
    @DisplayName("endpoint: /reset-password")
    class resetPassword {
        @Test
        @DisplayName("Deve redefinir a senha com sucesso")
        void shouldResetPasswordSuccessfully() throws Exception {
            user1.setPasswordResetToken(UUID.randomUUID().toString());
            user1.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user1);
            ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(user1.getPasswordResetToken() , "newPassword123");
            mockMvc.perform(post("/auth/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Operação realizada com sucesso"))
                    .andExpect(jsonPath("$.data").value("Senha redefinida com sucesso."));
        }
        @Test
        @DisplayName("Deve falhar ao redefinir a senha com token invalido")
        void shouldFailWhenResettingPasswordWithInvalidToken() throws Exception {
            user1.setPasswordResetToken(UUID.randomUUID().toString());
            user1.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user1);
            ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest("invalidToken" , "newPassword123");
            mockMvc.perform(post("/auth/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Token de redefinição de senha inválido."));
        }
        @Test
        @DisplayName("Deve falhar ao redefinir a senha com token expirado")
        void shouldFailWhenResettingPasswordWithExpiredToken() throws Exception {
            user1.setPasswordResetToken(UUID.randomUUID().toString());
            user1.setPasswordResetTokenExpiresAt(LocalDateTime.now().minusMinutes(1));
            userRepository.save(user1);
            ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(user1.getPasswordResetToken() , "newPassword123");
            mockMvc.perform(post("/auth/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Token expirado. Solicite uma nova redefinição."));
        }
        @Test
        @DisplayName("Deve falhar ao redefinir a senha com nova senha invalida")
        void shouldFailWhenResettingPasswordWithInvalidNewPassword() throws Exception {
            user1.setPasswordResetToken(UUID.randomUUID().toString());
            user1.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user1);
            ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(user1.getPasswordResetToken() , "123");
            mockMvc.perform(post("/auth/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro de validação: Senha deve ter no minimo 6 caracteres"));
        }
        @Test
        @DisplayName("Deve falhar ao redefinir a senha com token vazio")
        void shouldFailWhenResettingPasswordWithNullToken() throws Exception {
            ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(null , "newPassword123");
            mockMvc.perform(post("/auth/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro de validação: Token é obrigatório"));
        }
        @Test
        @DisplayName("Deve falhar ao redefinir a senha com nova senha nula")
        void shouldFailWhenResettingPasswordWithNullNewPassword() throws Exception {
            user1.setPasswordResetToken(UUID.randomUUID().toString());
            user1.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user1);
            ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(user1.getPasswordResetToken() , null);
            mockMvc.perform(post("/auth/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro de validação: Nova senha é obrigatória"));
        }
    }



    @Nested
    @DisplayName("endpoint: /access-token")
    class accessToken {
        @Test
        @DisplayName("Deve gerar um novo token de acesso com sucesso")
        void shouldGenerateNewAccessTokenSuccessfully() throws Exception {
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user1);
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiresAt(LocalDateTime.now().plusDays(10));
            refreshTokenRepository.save(refreshToken);
            RefreshTokenRequest request = new RefreshTokenRequest(refreshToken.getRefreshToken());
            mockMvc.perform(post("/auth/access-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isNotEmpty());
        }
        @Test
        @DisplayName("Deve falhar ao gerar um novo token com refresh token invalido")
        void shouldFailWhenRefreshTokenIsInvalid() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest("invalid-refresh-token");
            mockMvc.perform(post("/auth/access-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("Deve falhar ao gerar um novo token com refresh token expirado")
        void shouldFailWhenRefreshTokenIsExpired() throws Exception {
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user1);
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiresAt(LocalDateTime.now().minusDays(1));
            refreshTokenRepository.save(refreshToken);
            RefreshTokenRequest request = new RefreshTokenRequest(refreshToken.getRefreshToken());
            mockMvc.perform(post("/auth/access-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("Deve falhar ao gerar um novo token com refresh token nulo")
        void shouldFailWhenRefreshTokenIsNull() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest(null);
            mockMvc.perform(post("/auth/access-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Erro de validação: Token é obrigatorio"));
        }
    }
}

