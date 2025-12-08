package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.*;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.*;
import com.example.SpringSecurity.PostgreSQL.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegUserResponse>> register(@Valid @RequestBody RegUserRequest request) {
        RegUserResponse register = authService.register(request);
        ApiResponse<RegUserResponse> response = ApiResponse.success(register);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerifyUserResponse>> verify(@Valid @RequestBody VerifyUserRequest request) {
        VerifyUserResponse verify = authService.verifyUser(request);
        ApiResponse<VerifyUserResponse> response = ApiResponse.success(verify);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/verify-account")
    public ResponseEntity<ApiResponse<VerifyUserResponse>> verifyAccount(
            @RequestParam("email") String email,
            @RequestParam("code") String code) {
        VerifyUserRequest request = new VerifyUserRequest(email, code);
        VerifyUserResponse verify = authService.verifyUser(request);
        ApiResponse<VerifyUserResponse> response = ApiResponse.success(verify);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse login = authService.login(request);
        ApiResponse<LoginResponse> response = ApiResponse.success(login);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
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
    public ResponseEntity<ApiResponse<String>> refreshToken(@Valid @RequestBody String refreshToken) {
        String newAccessToken  = authService.newAccessToken(refreshToken);
        ApiResponse<String> response = ApiResponse.success(newAccessToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}