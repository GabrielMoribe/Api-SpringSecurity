package com.example.SpringSecurity.PostgreSQL.integrationTests.controller;

import com.example.SpringSecurity.PostgreSQL.config.JWTUserData;
import com.example.SpringSecurity.PostgreSQL.domain.dto.request.EmailRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.request.UpdateUserRequest;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.domain.enums.Roles;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;


    private User user;
    private User admin;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = new User();
        user.setName("user");
        user.setEmail("user@email.com");
        user.setPassword(passwordEncoder.encode("123123"));
        user.setEnabled(true);
        user.setRole(Roles.USER);
        user = userRepository.save(user);

        admin = new User();
        admin.setName("admin");
        admin.setEmail("admin@email.com");
        admin.setPassword(passwordEncoder.encode("123123"));
        admin.setEnabled(true);
        admin.setRole(Roles.ADMIN);
        admin = userRepository.save(admin);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    private void authenticateAsUser() {
        authenticateAs(user, "USER");
    }

    private void authenticateAsAdmin() {
        authenticateAs(admin, "ADMIN");
    }

    private void authenticateAs(User userToAuth, String role) {
        JWTUserData jwtUserData = JWTUserData.builder()
                .userId(userToAuth.getId())
                .email(userToAuth.getEmail())
                .role(role)
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        jwtUserData,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role)))
        );
    }


    @Nested
    @DisplayName("Acesso a endpoints sem estar autenticado")
    class AuthenticationTests {

        @Test
        @DisplayName("Deve retornar 401 Unauthorized quando tentar acessar perfil sem estar logado")
        void shouldReturnUnauthorizedWhenNotLoggedIn() throws Exception {
            mockMvc.perform(get("/users/profile"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("Deve retornar 401 ao tentar atualizar perfil sem autenticação")
        void shouldReturnUnauthorizedWhenUpdatingProfileWithoutAuth() throws Exception {
            UpdateUserRequest request = new UpdateUserRequest("Novo Nome", user.getEmail());
            mockMvc.perform(put("/users/profile/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("Deve retornar 401 ao tentar deletar perfil sem autenticação")
        void shouldReturnUnauthorizedWhenDeletingProfileWithoutAuth() throws Exception {
            mockMvc.perform(delete("/users/profile/delete"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("Deve retornar 401 ao tentar trocar email sem autenticação")
        void shouldReturnUnauthorizedWhenChangingEmailWithoutAuth() throws Exception {
            EmailRequest request = new EmailRequest("novo@email.com");
            mockMvc.perform(post("/users/profile/change-email")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }



    @Nested
    @DisplayName("GET /users/profile")
    class GetProfileTests {
        @Test
        @DisplayName("Deve retornar perfil do usuário quando autenticado")
        void shouldReturnUserProfileWhenAuthenticated() throws Exception {
            authenticateAsUser();
            mockMvc.perform(get("/users/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("user"))
                    .andExpect(jsonPath("$.data.email").value("user@email.com"));
        }

        @Test
        @DisplayName("Deve retornar erro 404 quando usuario nao for encontrado")
        void shouldReturnErrorWhenUserNotFound() throws Exception {
            authenticateAsUser();
            userRepository.delete(user);
            mockMvc.perform(get("/users/profile"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }


    @Nested
    @DisplayName("PUT /users/profile/update")
    class UpdateProfileTests {
        @Test
        @DisplayName("Deve atualizar nome do usuário com sucesso")
        void shouldUpdateUserNameSuccessfully() throws Exception {
            authenticateAsUser();

            UpdateUserRequest request = new UpdateUserRequest("Nome Atualizado", user.getEmail());
            mockMvc.perform(put("/users/profile/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("Nome Atualizado"));
        }
        @Test
        @DisplayName("Deve retornar erro quando nome está vazio")
        void shouldReturnErrorWhenNameIsEmpty() throws Exception {
            authenticateAsUser();
            UpdateUserRequest request = new UpdateUserRequest("", user.getEmail());
            mockMvc.perform(put("/users/profile/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("Deve retornar erro quando nome é nulo")
        void shouldReturnErrorWhenNameIsNull() throws Exception {
            authenticateAsUser();
            mockMvc.perform(put("/users/profile/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("Deve retornar erro 404 quando usuario nao for encontrado")
        void shouldReturnErrorWhenUserNotFound() throws Exception {
            authenticateAsUser();
            userRepository.delete(user);
            UpdateUserRequest request = new UpdateUserRequest("Novo Nome", "novo@email.com");
            mockMvc.perform(put("/users/profile/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }



    @Nested
    @DisplayName("POST /users/profile/change-email")
    class ChangeEmailTests {
        @Test
        @DisplayName("Deve permitir troca de email com sucesso")
        void shouldInitiateEmailChangeSuccessfully() throws Exception {
            authenticateAsUser();
            EmailRequest request = new EmailRequest("novoemail@email.com");
            mockMvc.perform(post("/users/profile/change-email")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
        @Test
        @DisplayName("Deve retornar erro quando email é inválido")
        void shouldReturnErrorWhenEmailIsInvalid() throws Exception {
            authenticateAsUser();
            EmailRequest request = new EmailRequest("user.com");
            mockMvc.perform(post("/users/profile/change-email")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("Deve retornar erro quando email já está cadastrado")
        void shouldReturnErrorWhenEmailAlreadyExists() throws Exception {
            authenticateAsUser();
            EmailRequest request = new EmailRequest("admin@email.com");
            mockMvc.perform(post("/users/profile/change-email")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("Deve retornar erro quando email é vazio")
        void shouldReturnErrorWhenEmailIsEmpty() throws Exception {
            authenticateAsUser();
            EmailRequest request = new EmailRequest("");
            mockMvc.perform(post("/users/profile/change-email")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("Deve retornar erro quando novo email é igual ao atual")
        void shouldReturnErrorWhenNewEmailSameAsCurrent() throws Exception {
            authenticateAsUser();
            EmailRequest request = new EmailRequest("user@email.com");
            mockMvc.perform(post("/users/profile/change-email")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }



    @Nested
    @DisplayName("GET /users/profile/change-email/confirm")
    class ConfirmEmailChangeTests {
        @Test
        @DisplayName("Deve confirmar troca de email com sucesso")
        void shouldConfirmEmailChangeSuccessfully() throws Exception {
            authenticateAsUser();
            String token = UUID.randomUUID().toString();
            user.setNewEmailToken(token);
            user.setNewEmailTokenExpiresAt(LocalDateTime.now().plusHours(1));
            userRepository.save(user);
            mockMvc.perform(get("/users/profile/change-email/confirm")
                            .param("token", token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
        @Test
        @DisplayName("Deve retornar erro quando token é inválido")
        void shouldReturnErrorWhenTokenIsInvalid() throws Exception {
            mockMvc.perform(get("/users/profile/change-email/confirm")
                            .param("token", "token-invalido"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("Deve retornar erro quando token não existe")
        void shouldReturnErrorWhenTokenDoesNotExist() throws Exception {
            mockMvc.perform(get("/users/profile/change-email/confirm")
                            .param("token", UUID.randomUUID().toString()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("Deve retornar erro quando token está expirado")
        void shouldReturnErrorWhenTokenIsExpired() throws Exception {
            authenticateAsUser();
            String token = UUID.randomUUID().toString();
            user.setNewEmailToken(token);
            user.setNewEmailTokenExpiresAt(LocalDateTime.now().minusHours(1));
            userRepository.save(user);
            mockMvc.perform(get("/users/profile/change-email/confirm")
                            .param("token", token))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }


    @Nested
    @DisplayName("DELETE /users/profile/delete")
    class DeleteProfileTests {
        @Test
        @DisplayName("Deve deletar usuário com sucesso")
        void shouldDeleteUserSuccessfully() throws Exception {
            authenticateAsUser();
            mockMvc.perform(delete("/users/profile/delete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
        @Test
        @DisplayName("Deve retornar erro quando usuario nao for encontrado")
        void shouldReturnErrorWhenUserNotFound() throws Exception {
            authenticateAsUser();
            userRepository.delete(user);
            mockMvc.perform(delete("/users/profile/delete"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }


    @Nested
    @DisplayName("Testes de Autorização")
    class AuthorizationTests {
        @Test
        @DisplayName("Deve negar acesso a endpoint de ADMIN para usuário USER")
        void shouldDenyUserAccessToAdminRoute() throws Exception {
            authenticateAsUser();
            mockMvc.perform(get("/admin/allusers"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("Deve permitir acesso a endpoint de ADMIN para usuário Admin")
        void shouldAllowAdminAccessToAdminRoute() throws Exception {
            authenticateAsAdmin();
            mockMvc.perform(get("/admin/allusers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
        @Test
        @DisplayName("Deve negar acesso a endpoint de usuário para usuário não autenticado")
        void shouldDenyAccessToUserRoute() throws Exception {
            mockMvc.perform(get("/users/profile"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false));
        }
        @Test
        @DisplayName("USER deve acessar endpoints de usuário")
        void shouldAllowUserAccessToUserEndpoints() throws Exception {
            authenticateAsUser();
            mockMvc.perform(get("/users/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }
}