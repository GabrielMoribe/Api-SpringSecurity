package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.UpdateUserRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ApiResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.UpdateUserResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> getUserProfile() {
        User user = userService.findUser();
        ApiResponse<UpdateUserResponse> response = ApiResponse.success(new UpdateUserResponse(user.getName(), user.getEmail()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PutMapping("/profile/update")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateUserProfile(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        User updatedUser = userService.updateUser(updateUserRequest);
        ApiResponse<UpdateUserResponse> response = ApiResponse.success(new UpdateUserResponse(updatedUser.getName(), updatedUser.getEmail()));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @DeleteMapping("/profile/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUserProfile() {
        userService.deleteUser();
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
