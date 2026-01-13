package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.*;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.*;
import com.example.SpringSecurity.PostgreSQL.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegUserRequest request) {
        UserResponse register = authService.register(request);
        ApiResponse<UserResponse> response = ApiResponse.success(register);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify(@Valid @RequestBody VerifyUserRequest request) {
        authService.verifyUser(request);
        ApiResponse<String> response = ApiResponse.success("Usuario verificado com sucesso.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/verify-account")
    public ResponseEntity<ApiResponse<String>> verifyAccount(
            @RequestParam(value = "email" , required = true) String email,
            @RequestParam(value = "code"  , required = true) String code) {
        VerifyUserRequest request = new VerifyUserRequest(email, code);
        authService.verifyUser(request);
        ApiResponse<String> response = ApiResponse.success("Usuario verificado com sucesso.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/verify-account/resend")
    public ResponseEntity<ApiResponse<String>> resendVerificationCode(@Valid @RequestBody EmailRequest request) {
        authService.resendVerificationCode(request.email());
        ApiResponse<String> response = ApiResponse.success("Um novo codigo de verificacao foi enviado para " + request.email());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse login = authService.login(request);
        ApiResponse<LoginResponse> response = ApiResponse.success(login);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String accessToken) {
        authService.logout(accessToken);
        ApiResponse<String> response = ApiResponse.success("Logout realizado com sucesso");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody EmailRequest request) {
        authService.forgotPassword(request);
        ApiResponse<String> response = ApiResponse.success("Um email foi enviado para " + request.email() + " com instruções para redefinir sua senha.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        ApiResponse<String> response = ApiResponse.success("Senha redefinida com sucesso.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/access-token")
    public ResponseEntity<ApiResponse<String>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String newAccessToken  = authService.newAccessToken(request.refreshToken());
        ApiResponse<String> response = ApiResponse.success(newAccessToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}