package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.ChangeEmailRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.request.UpdateUserRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ApiResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.UpdateUserResponse;
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
    public ResponseEntity<ApiResponse<UpdateUserResponse>> getUserProfile() {
        User user = userService.findUser();
        ApiResponse<UpdateUserResponse> response = ApiResponse.success(new UpdateUserResponse(user.getName(), user.getEmail()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PutMapping("/profile/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateUserProfile(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        User updatedUser = userService.updateUser(updateUserRequest);
        ApiResponse<UpdateUserResponse> response = ApiResponse.success(new UpdateUserResponse(updatedUser.getName(), updatedUser.getEmail()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/profile/change-email")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> changeEmail(@Valid @RequestBody ChangeEmailRequest changeEmailRequest) {
        userService.changeEmail(changeEmailRequest.newEmail());
        ApiResponse<String> response = ApiResponse.success("Enviamos um email de confirmacao");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/profile/change-email/confirm")
    //@PreAuthorize("hasRole('USER')")
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
