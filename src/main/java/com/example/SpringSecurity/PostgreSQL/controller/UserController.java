package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.EmailRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.request.UpdateUserRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ApiResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.UserResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile() {
        User user = userService.findUser();
        ApiResponse<UserResponse> response = ApiResponse.success(new UserResponse(user.getName(), user.getEmail()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PutMapping("/profile/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        User updatedUser = userService.updateUser(updateUserRequest);
        ApiResponse<UserResponse> response = ApiResponse.success(new UserResponse(updatedUser.getName(), updatedUser.getEmail()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping("/profile/change-email")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> changeEmail(@Valid @RequestBody EmailRequest changeEmailRequest) {
        userService.changeEmail(changeEmailRequest.email());
        ApiResponse<String> response = ApiResponse.success("Enviamos um email de confirmacao");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/profile/change-email/confirm")
    public ResponseEntity<ApiResponse<String>> verifyNewEmail(
            @RequestParam("token") String token){
        userService.confirmEmailChange(token);
        ApiResponse<String> response = ApiResponse.success("Email alterado com sucesso!");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @DeleteMapping("/profile/delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> deleteUserProfile() {
        userService.deleteUser();
        ApiResponse<String> response = ApiResponse.success("Usuario deletado com sucesso");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
