package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.LoginRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.request.RegUserRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.request.VerifyUserRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ApiResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.LoginResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.RegUserResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.VerifyUserResponse;
import com.example.SpringSecurity.PostgreSQL.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse login = authService.login(request);
        ApiResponse<LoginResponse> response = ApiResponse.success(login);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}